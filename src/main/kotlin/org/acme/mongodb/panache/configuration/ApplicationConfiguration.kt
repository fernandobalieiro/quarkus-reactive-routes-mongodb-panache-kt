package org.acme.mongodb.panache.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent

@ApplicationScoped
class ApplicationConfiguration {

    @Dependent
    fun jacksonKotlinModule(): ObjectMapper {
        return jacksonObjectMapper()
    }
}
