package org.acme.mongodb.panache.route

import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.acme.mongodb.panache.controller.PersonController
import org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@ApplicationScoped
class PersonRoutes(
        val personController: PersonController
) {
    fun init(@Observes router: Router) {
        router.route().handler(BodyHandler.create())
        router.options().produces(APPLICATION_JSON).consumes(APPLICATION_JSON)
        router.errorHandler(SC_INTERNAL_SERVER_ERROR) { ctx -> ctx.response().end("Got something bad") }

        router.get("/persons").handler { rc -> personController.list(rc) }
        router.post("/persons").handler { rc -> personController.create(rc) }
        router.put("/persons/:id").handler { rc -> personController.update(rc) }
        router.get("/persons/:id").handler { rc -> personController.getById(rc) }
        router.delete("/persons/:id").handler { rc -> personController.delete(rc) }
    }
}
