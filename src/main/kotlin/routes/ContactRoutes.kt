package pro.ralan.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pro.ralan.models.ContactFormRequest
import pro.ralan.models.ContactFormResponse
import pro.ralan.services.TelegramService

fun Route.contactRoutes() {
    post("/contact") {
        try {
            val formData = call.receive<ContactFormRequest>()
            
            val success = TelegramService.sendContactForm(formData)
            
            if (success) {
                call.respond(
                    HttpStatusCode.OK,
                    ContactFormResponse(
                        success = true,
                        message = "Заявка успешно отправлена"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ContactFormResponse(
                        success = false,
                        message = "Не удалось отправить заявку"
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ContactFormResponse(
                    success = false,
                    message = "Ошибка обработки запроса: ${e.message}"
                )
            )
        }
    }
}

