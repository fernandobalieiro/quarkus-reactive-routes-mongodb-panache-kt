package org.acme.mongodb.panache

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType.JSON
import org.acme.mongodb.panache.database.entity.Person
import org.acme.mongodb.panache.database.entity.Status.ALIVE
import org.acme.mongodb.panache.database.repository.PersonRepository
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.time.Duration
import javax.inject.Inject

@QuarkusTest
@TestInstance(PER_CLASS)
class PersonResourceTest {

    @Inject
    lateinit var personRepository: PersonRepository

    private lateinit var person1: Person
    private lateinit var person2: Person

    @BeforeAll
    fun setup() {
        person1 = Person(
                name = "Person 1",
                status = ALIVE
        )

        person2 = Person(
                name = "Person 2",
                status = ALIVE
        )

        val people = listOf(
                person1,
                person2
        )

        personRepository.persist(people).await().atMost(Duration.ofSeconds(2))
    }

    @AfterAll
    fun tearDown() {
        personRepository.deleteAll().await().atMost(Duration.ofSeconds(2))
    }

    @Test
    fun testGetAllEndpoint() {
        given()
                .contentType(JSON)
                .`when`().get("/persons")
                .then()
                .statusCode(200)
                .body("$.size()", `is`(2))
    }

    @Test
    fun testGetByIdEndpoint() {
        given()
                .contentType(JSON)
                .`when`().get("/persons/{id}", person1.id.toString())
                .then()
                .statusCode(200)
                .body("name", `is`("Person 1"))
    }

    @Test
    fun testCreateEndpoint() {
        val payload = mapOf(
                "name" to "Person 3",
                "status" to "ALIVE"
        )

        given()
                .body(payload)
                .contentType(JSON)
                .`when`().post("/persons")
                .then()
                .statusCode(201)
                .body("name", `is`("Person 3"))
    }

    @Test
    fun testUpdateEndpoint() {
        val payload = mapOf(
                "name" to "Person 2 Updated",
                "status" to "DEAD"
        )

        given()
                .body(payload)
                .contentType(JSON)
                .`when`().put("/persons/{id}", person2.id.toString())
                .then()
                .statusCode(200)
                .body("name", `is`("Person 2 Updated"))
    }

    @Test
    fun testDeleteEndpoint() {
        val personToDelete = Person(
                name = "Person to Delete",
                status = ALIVE
        )

        personRepository.persist(personToDelete).await().atMost(Duration.ofSeconds(2))

        given()
                .contentType(JSON)
                .`when`().delete("/persons/{id}", personToDelete.id.toString())
                .then()
                .statusCode(200)
    }
}
