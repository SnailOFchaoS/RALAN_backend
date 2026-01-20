package pro.ralan

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
}
