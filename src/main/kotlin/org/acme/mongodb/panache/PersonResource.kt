package org.acme.mongodb.panache

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.bson.types.ObjectId
import org.jboss.resteasy.annotations.SseElementType
import org.jboss.resteasy.annotations.jaxrs.PathParam
import java.net.URI
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.MediaType.SERVER_SENT_EVENTS
import javax.ws.rs.core.Response

@Path("/persons")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class PersonResource {

    @Inject
    lateinit var personRepository: PersonRepository

    @GET
    @Path("/stream")
    @Produces(SERVER_SENT_EVENTS)
    @SseElementType(APPLICATION_JSON)
    fun streamPersons(): Multi<Person> {
        return personRepository.streamAll()
    }

    @GET
    fun get(): Uni<List<Person>> {
        return personRepository.findAll().list()
    }

    @GET
    @Path("{id}")
    fun getSingle(@PathParam("id") id: String?): Uni<Response> {
        return personRepository.findById(ObjectId(id))
                .onItem().apply { person -> if (person?.id != null) Response.ok(person) else Response.status(Response.Status.NOT_FOUND) }
                .onItem().apply { response -> response.build() }
    }

    @POST
    fun create(person: Person): Uni<Response> {
        return personRepository.persist(person)
                .onItem().apply { if (person.id != null) Response.created(URI("/persons/${person.id}")).entity(person) else Response.status(Response.Status.NOT_FOUND) }
                .onItem().apply { response -> response.build() }
    }

    @PUT
    @Path("{id}")
    fun update(@PathParam("id") id: String?, person: Person): Uni<Response> {
        person.id = ObjectId(id)

        return personRepository.update(person)
                .onItem().apply { if (person.id != null) Response.ok(person) else Response.status(Response.Status.NOT_FOUND) }
                .onItem().apply { response -> response.build() }
    }

    @DELETE
    @Path("{id}")
    fun delete(@PathParam("id") id: String?): Uni<Response> {
        return personRepository.deleteById(ObjectId(id))
                .onItem().apply { status: Boolean? -> Response.status(Response.Status.OK).build() }
    }
}
