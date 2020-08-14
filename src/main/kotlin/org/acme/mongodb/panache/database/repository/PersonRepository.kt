package org.acme.mongodb.panache.database.repository

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository
import org.acme.mongodb.panache.database.entity.Person
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonRepository : ReactivePanacheMongoRepository<Person>
