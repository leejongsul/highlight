package com.leejongsul.plugins

import com.leejongsul.routes.feeds.getFeeds
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    install(Resources)

    routing {
        getFeeds(get())
    }
}
