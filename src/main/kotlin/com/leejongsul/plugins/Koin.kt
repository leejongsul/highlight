package com.leejongsul.plugins

import com.leejongsul.service.FeedService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(module {
            single { FeedService() }
        })
    }
}