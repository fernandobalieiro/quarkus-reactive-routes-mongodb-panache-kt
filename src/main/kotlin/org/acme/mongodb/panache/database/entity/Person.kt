package org.acme.mongodb.panache.database.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.quarkus.mongodb.panache.MongoEntity
import io.quarkus.mongodb.panache.PanacheMongoEntity
import org.bson.codecs.pojo.annotations.BsonProperty
import java.util.Date

@MongoEntity(collection = "people")
@JsonNaming(SnakeCaseStrategy::class)
data class Person(
        var name: String,

        @field: JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        @field: BsonProperty("birth_date")
        var birthDate: Date? = Date(),

        var status: Status
) : PanacheMongoEntity()

enum class Status {
    ALIVE, DEAD
}
