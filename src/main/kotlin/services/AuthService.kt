package pro.ralan.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import pro.ralan.database.tables.UsersTable
import pro.ralan.models.User
import java.time.LocalDateTime
import java.util.*

object AuthService {
    
    // JWT настройки - в продакшене лучше вынести в переменные окружения
    private val jwtSecret = System.getenv("JWT_SECRET") ?: "ralan-super-secret-key-change-in-production"
    private val jwtIssuer = "ralan.pro"
    private val jwtAudience = "ralan-api"
    private val jwtExpirationMs = 24 * 60 * 60 * 1000 // 24 часа
    
    val jwtAlgorithm: Algorithm = Algorithm.HMAC256(jwtSecret)
    val jwtVerifier = JWT.require(jwtAlgorithm)
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .build()
    
    fun createTable() {
        transaction {
            SchemaUtils.create(UsersTable)
        }
    }
    
    // Создать первого админа, если таблица пустая
    fun seedDefaultAdmin() {
        val defaultUsername = System.getenv("ADMIN_USERNAME") ?: "admin"
        val defaultPassword = System.getenv("ADMIN_PASSWORD") ?: "j-sFwG8tA1"
        
        transaction {
            val exists = UsersTable.selectAll().count() > 0
            if (!exists) {
                createUser(defaultUsername, defaultPassword)
                println("Default admin created: $defaultUsername")
            }
        }
    }
    
    fun createUser(username: String, password: String): User? {
        return try {
            val userId = UUID.randomUUID()
            val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
            val now = LocalDateTime.now()
            
            transaction {
                UsersTable.insert {
                    it[id] = userId
                    it[UsersTable.username] = username
                    it[UsersTable.passwordHash] = passwordHash
                    it[createdAt] = now
                }
            }
            
            User(
                id = userId.toString(),
                username = username,
                createdAt = now.toString()
            )
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
            null
        }
    }
    
    fun authenticate(usernameInput: String, password: String): String? {
        return transaction {
            val row = UsersTable.select { UsersTable.username eq usernameInput }
                .singleOrNull()
            
            if (row != null && BCrypt.checkpw(password, row[UsersTable.passwordHash])) {
                generateToken(row[UsersTable.id].toString(), usernameInput)
            } else {
                null
            }
        }
    }
    
    private fun generateToken(userId: String, username: String): String {
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpirationMs))
            .sign(jwtAlgorithm)
    }
    
    fun getUserIdFromToken(token: String): String? {
        return try {
            val decoded = jwtVerifier.verify(token)
            decoded.getClaim("userId").asString()
        } catch (e: Exception) {
            null
        }
    }
}
