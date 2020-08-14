package org.acme.mongodb.panache.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.acme.mongodb.panache.database.entity.Person
import org.acme.mongodb.panache.database.entity.Status
import java.util.Date

@JsonNaming(SnakeCaseStrategy::class)
data class PersonResponse(
        val id: String,

        val name: String,

        @field: JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val birthDate: Date,

        val status: Status
) {
    constructor(person: Person) : this(
            id = person.id.toString(),
            name = person.name,
            birthDate = person.birthDate!!,
            status = person.status
    )
}
