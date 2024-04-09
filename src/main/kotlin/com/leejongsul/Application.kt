package com.leejongsul

import com.leejongsul.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureStatusPages()
    configureRouting()
}
