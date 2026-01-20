package pro.ralan

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pro.ralan.routes.authRoutes
import pro.ralan.routes.contactRoutes
import pro.ralan.routes.offerRoutes

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        
        // Подключаем роуты из отдельных файлов
        authRoutes()
        contactRoutes()
        offerRoutes()
    }
}
