package pro.ralan.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pro.ralan.models.LoginRequest
import pro.ralan.models.LoginResponse
import pro.ralan.services.AuthService

fun Route.authRoutes() {
    route("/auth") {
        
        // POST /auth/login - получить JWT токен
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                
                val token = AuthService.authenticate(request.username, request.password)
                
                if (token != null) {
                    call.respond(
                        HttpStatusCode.OK,
                        LoginResponse(
                            success = true,
                            message = "Успешный вход",
                            token = token
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        LoginResponse(
                            success = false,
                            message = "Неверный логин или пароль"
                        )
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    LoginResponse(
                        success = false,
                        message = "Ошибка: ${e.message}"
                    )
                )
            }
        }
    }
}
