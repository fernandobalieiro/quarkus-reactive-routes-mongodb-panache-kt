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
        savePerson(rc)
    }

    fun update(rc: RoutingContext) {
        savePerson(rc)
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

        personRepository.findById(ObjectId(personId)).subscribe().with { person ->
            if (person != null) {
                val personResponse = PersonResponse(person)
                rc.response().end(jacksonKotlinModule.writeValueAsString(personResponse))
            } else {
                rc.response().setStatusCode(SC_NOT_FOUND).end()
            }
        }
    }

    private fun savePerson(rc: RoutingContext) {
        val person = jacksonKotlinModule.readValue<Person>(rc.bodyAsString)

        personRepository.persist(person).subscribe().with {
            val personResponse = PersonResponse(person)
            rc.response().setStatusCode(SC_CREATED).end(jacksonKotlinModule.writeValueAsString(personResponse))
        }
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
