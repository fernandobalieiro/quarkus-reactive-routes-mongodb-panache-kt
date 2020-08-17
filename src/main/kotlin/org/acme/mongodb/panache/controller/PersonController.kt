package org.acme.mongodb.panache.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.vertx.ext.web.RoutingContext
import org.acme.mongodb.panache.database.entity.Person
import org.acme.mongodb.panache.database.repository.PersonRepository
import org.acme.mongodb.panache.response.PersonResponse
import org.apache.http.HttpStatus.SC_CREATED
import org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import org.apache.http.HttpStatus.SC_NOT_FOUND
import org.apache.http.HttpStatus.SC_OK
import org.bson.types.ObjectId
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonController(
        val personRepository: PersonRepository,
        val jacksonKotlinModule: ObjectMapper
) {
    fun create(rc: RoutingContext) {
        val person = jacksonKotlinModule.readValue<Person>(rc.bodyAsString)

        personRepository.persist(person).subscribe().with {
            val personResponse = PersonResponse(person)
            rc.response().setStatusCode(SC_CREATED).end(jacksonKotlinModule.writeValueAsString(personResponse))
        }
    }

    fun update(rc: RoutingContext) {
        val personId = rc.pathParam("id")
        val payload = jacksonKotlinModule.readValue<Person>(rc.bodyAsString)

        personRepository.findById(ObjectId(personId))
                .onItem().ifNull().failWith {
                    rc.response().setStatusCode(SC_NOT_FOUND).end()
                    IllegalArgumentException("An Invalid Person Id was provided")
                }
                .onItem().ifNotNull().apply { person ->
                    person.birthDate = payload.birthDate
                    person.name = payload.name
                    person.status = payload.status

                    personRepository.update(person).subscribe().with(
                            {
                                rc.response().setStatusCode(SC_OK).end(jacksonKotlinModule.writeValueAsString(PersonResponse(person)))
                            },
                            { t: Throwable ->
                                t.printStackTrace()
                                rc.response().setStatusCode(SC_INTERNAL_SERVER_ERROR).end()
                            }
                    )
                }
                .subscribe().with { }
    }

    fun list(rc: RoutingContext) {
        personRepository.findAll().list<Person>().subscribe().with { people ->
            val peopleResponse = people.map { person ->
                PersonResponse(person)
            }
            rc.response().end(jacksonKotlinModule.writeValueAsString(peopleResponse))
        }
    }

    fun getById(rc: RoutingContext) {
        val personId = rc.pathParam("id")

        personRepository.findById(ObjectId(personId))
                .onItem().ifNull().failWith {
                    rc.response().setStatusCode(SC_NOT_FOUND).end()
                    IllegalArgumentException("An Invalid Person Id was provided")
                }
                .onItem().ifNotNull().apply { person ->
                    rc.response().end(jacksonKotlinModule.writeValueAsString(PersonResponse(person)))
                }
                .subscribe().with { }
    }

    fun delete(rc: RoutingContext) {
        val personId = rc.pathParam("id")

        personRepository.deleteById(ObjectId(personId)).subscribe().with { success ->
            if (success) {
                rc.response().setStatusCode(SC_OK).end()
            } else {
                rc.response().setStatusCode(SC_INTERNAL_SERVER_ERROR).end()
            }
        }
    }
}
