package pro.ralan

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import pro.ralan.services.AuthService

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(AuthService.jwtVerifier)
            
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                val username = credential.payload.getClaim("username").asString()
                
                if (userId != null && username != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("success" to false, "message" to "Требуется авторизация")
                )
            }
        }
    }
}
