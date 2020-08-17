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
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonController(
        val personRepository: PersonRepository,
        val jacksonKotlinModule: ObjectMapper
) {
    fun create(rc: RoutingContext) {
        val person = jacksonKotlinModule.readValue<Person>(rc.bodyAsString)

        personRepository.persist(person)
                .onItem().apply {
                    val personResponse = PersonResponse(person)
                    rc.response().setStatusCode(SC_CREATED).end(jacksonKotlinModule.writeValueAsString(personResponse))
                }
                .onFailure().invoke { t: Throwable ->
                    handleError(t, rc)
                }
                .subscribe().with { }
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

                    personRepository.update(person)
                            .onItem().apply {
                                rc.response().setStatusCode(SC_OK).end(jacksonKotlinModule.writeValueAsString(PersonResponse(person)))
                            }
                            .onFailure().invoke { t: Throwable ->
                                handleError(t, rc)
                            }
                            .subscribe().with {  }
                }
                .onFailure().invoke { t: Throwable ->
                    handleError(t, rc)
                }
                .subscribe().with { }
    }

    fun list(rc: RoutingContext) {
        personRepository.findAll().list<Person>()
                .onItem().apply { people ->
                    val peopleResponse = people.map { person ->
                        PersonResponse(person)
                    }
                    rc.response().end(jacksonKotlinModule.writeValueAsString(peopleResponse))
                }
                .onFailure().invoke { t: Throwable ->
                    handleError(t, rc)
                }
                .subscribe().with { }
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
                .onFailure().invoke { t: Throwable ->
                    handleError(t, rc)
                }
                .subscribe().with { }
    }

    fun delete(rc: RoutingContext) {
        val personId = rc.pathParam("id")

        personRepository.deleteById(ObjectId(personId))
                .onItem().apply { success ->
                    if (success) {
                        rc.response().setStatusCode(SC_OK).end()
                    } else {
                        rc.response().setStatusCode(SC_INTERNAL_SERVER_ERROR).end()
                    }
                }
                .onFailure().invoke { t: Throwable ->
                    handleError(t, rc)
                }
                .subscribe().with { }
    }

    private fun handleError(t: Throwable, rc: RoutingContext) {
        logger.error("Unexpected Error: {}", t.message, t)
        rc.response().setStatusCode(SC_INTERNAL_SERVER_ERROR).end()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PersonController::class.java)
    }
}
