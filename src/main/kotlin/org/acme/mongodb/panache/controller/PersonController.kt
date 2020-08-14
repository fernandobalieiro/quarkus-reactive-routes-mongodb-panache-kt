package org.acme.mongodb.panache.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.web.RoutingContext
import org.acme.mongodb.panache.database.entity.Person
import org.acme.mongodb.panache.database.repository.PersonRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonController(
        val personRepository: PersonRepository,
        val jacksonKotlinModule: ObjectMapper
) {
    fun list(rc: RoutingContext) {
        personRepository.findAll().list<Person>().subscribe().with { people ->
            rc.response().end(jacksonKotlinModule.writeValueAsString(people))
        }
    }
}
