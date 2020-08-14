package org.acme.mongodb.panache.database.entity

import io.quarkus.mongodb.panache.MongoEntity
import io.quarkus.mongodb.panache.PanacheMongoEntity
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.Date

@MongoEntity(collection = "people")
data class Person(
        var name: String,

        @field: BsonProperty("birth_date")
        var birthDate: Date? = Date(),

        var status: Status
) : PanacheMongoEntity()

enum class Status {
    ALIVE, DEAD
}
