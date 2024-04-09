package com.leejongsul.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if(cause is NotFoundException) {
                call.respondText(text = "${cause.message}" , status = HttpStatusCode.NotFound)
            } else {
                call.respondText(text = "${cause.message}" , status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
