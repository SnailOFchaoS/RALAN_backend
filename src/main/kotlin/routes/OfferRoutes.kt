package pro.ralan.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pro.ralan.models.*
import pro.ralan.services.OfferService

fun Route.offerRoutes() {
    route("/offers") {
        
        // ===== ПУБЛИЧНЫЕ РОУТЫ (без авторизации) =====
        
        // GET /offers - получить все предложения
        get {
            val offers = OfferService.getAll()
            call.respond(
                HttpStatusCode.OK,
                OffersListResponse(success = true, data = offers)
            )
        }
        
        // GET /offers/{id} - получить одно предложение по UUID
        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    OfferResponse(success = false, message = "ID не указан")
                )
                return@get
            }
            
            val offer = OfferService.getById(id)
            if (offer == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    OfferResponse(success = false, message = "Предложение не найдено")
                )
                return@get
            }
            
            call.respond(
                HttpStatusCode.OK,
                OfferResponse(success = true, message = "OK", data = offer)
            )
        }
        
        // ===== ЗАЩИЩЁННЫЕ РОУТЫ (требуют JWT) =====
        
        authenticate("auth-jwt") {
            
            // POST /offers - создать новое предложение
            post {
                try {
                    val request = call.receive<CreateOfferRequest>()
                    val offer = OfferService.create(request)
                    
                    call.respond(
                        HttpStatusCode.Created,
                        OfferResponse(success = true, message = "Предложение создано", data = offer)
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        OfferResponse(success = false, message = "Ошибка: ${e.message}")
                    )
                }
            }
            
            // PUT /offers/{id} - обновить предложение
            put("/{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        OfferResponse(success = false, message = "ID не указан")
                    )
                    return@put
                }
                
                try {
                    val request = call.receive<UpdateOfferRequest>()
                    val updated = OfferService.update(id, request)
                    
                    if (updated == null) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            OfferResponse(success = false, message = "Предложение не найдено")
                        )
                        return@put
                    }
                    
                    call.respond(
                        HttpStatusCode.OK,
                        OfferResponse(success = true, message = "Предложение обновлено", data = updated)
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        OfferResponse(success = false, message = "Ошибка: ${e.message}")
                    )
                }
            }
            
            // DELETE /offers/{id} - удалить предложение
            delete("/{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        OfferResponse(success = false, message = "ID не указан")
                    )
                    return@delete
                }
                
                val deleted = OfferService.delete(id)
                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        OfferResponse(success = true, message = "Предложение удалено")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        OfferResponse(success = false, message = "Предложение не найдено")
                    )
                }
            }
        }
    }
}
