package pro.ralan

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import pro.ralan.services.AuthService
import pro.ralan.services.OfferService

fun Application.configureDatabase() {
    val config = HikariConfig().apply {

        // для работы на сервере
        jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/ralan_db"
        
        // для работы локально
        //jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:${System.getenv("DB_PORT") ?: "5432"}/ralan_db"
        
        driverClassName = "org.postgresql.Driver"
        username = "admin"
        password = System.getenv("DB_PASSWORD") ?: "3o-NpxNMll"
        maximumPoolSize = 10
    }

    Database.connect(HikariDataSource(config))
    
    // Создаём таблицы, если не существуют
    AuthService.createTable()
    OfferService.createTable()
    
    // Создаём админа по умолчанию, если нет пользователей
    AuthService.seedDefaultAdmin()

    log.info("Database connected successfully")
}
