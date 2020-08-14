package org.acme.mongodb.panache.route

import io.vertx.ext.web.Router
import org.acme.mongodb.panache.controller.PersonController
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class PersonRoutes(
        val personController: PersonController
) {

    fun init(@Observes router: Router) {
        router.get("/persons_reactive").handler {  rc -> personController.list(rc) }
    }
}
