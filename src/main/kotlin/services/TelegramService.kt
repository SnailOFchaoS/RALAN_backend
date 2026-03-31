package pro.ralan.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import pro.ralan.models.ContactFormRequest

object TelegramService {
    private const val BOT_TOKEN = "6508038158:AAGLibM_Qdr77EeNXyjYOcdDTDA5jkfKjXE"
    private val CHAT_IDS = listOf("674421657", "694304882")
    
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
    
    suspend fun sendContactForm(formData: ContactFormRequest): Boolean {
        val message = buildMessage(formData)
        
        val results = CHAT_IDS.map { chatId ->
            sendToChat(chatId, message)
        }
        
        return results.any { it }
    }
    
    private fun buildMessage(formData: ContactFormRequest): String {
        val dateTime = java.time.ZonedDateTime.now(java.time.ZoneOffset.ofHours(3))
            .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        
        return """
            |🏃 <b>Новая заявка с сайта RALAN</b>
            |
            |👤 <b>ФИО:</b> ${formData.fullName}
            |📱 <b>Телефон:</b> ${formData.phone}
            |💬 <b>Комментарий:</b> ${formData.comment}
            |📅 <b>Дата:</b> $dateTime
        """.trimMargin()
    }
    
    private suspend fun sendToChat(chatId: String, message: String): Boolean {
        return try {
            val response = client.post("https://api.telegram.org/bot$BOT_TOKEN/sendMessage") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "chat_id" to chatId,
                    "text" to message,
                    "parse_mode" to "HTML"
                ))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            println("Failed to send message to chat $chatId: ${e.message}")
            false
        }
    }
}
