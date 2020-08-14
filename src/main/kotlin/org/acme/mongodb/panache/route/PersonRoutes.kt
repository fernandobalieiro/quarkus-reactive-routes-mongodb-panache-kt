package org.acme.mongodb.panache.route

import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.acme.mongodb.panache.controller.PersonController
import org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class PersonRoutes(
        val personController: PersonController
) {
    fun init(@Observes router: Router) {
        router.errorHandler(SC_INTERNAL_SERVER_ERROR) { ctx -> ctx.response().end("Got something bad") }
        router.route().handler(BodyHandler.create())

        router.get("/persons").handler { rc -> personController.list(rc) }
        router.post("/persons").handler { rc -> personController.create(rc) }
        router.put("/persons").handler { rc -> personController.update(rc) }
        router.get("/persons/:id").handler { rc -> personController.getById(rc) }
        router.delete("/persons/:id").handler { rc -> personController.delete(rc) }
    }
}
