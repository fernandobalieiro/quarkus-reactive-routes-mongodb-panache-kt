package org.acme.mongodb.panache.filter

import io.quarkus.vertx.http.runtime.filters.Filters
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class ExampleFilters {

    fun registerMyFilter(@Observes filters: Filters) {
        filters.register({ rc: RoutingContext ->
            logger.info("Request intercepted for: method={}, path={}", rc.request().method(), rc.request().path())
            rc.response().putHeader("X-Header", "Request intercepted")
            rc.next()
        }, 100)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ExampleFilters::class.java)
    }
}
