package org.acme.mongodb.panache

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PersonRepository : ReactivePanacheMongoRepository<Person>
