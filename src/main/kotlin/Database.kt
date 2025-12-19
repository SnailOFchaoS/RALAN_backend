package pro.ralan

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/ralan"
        driverClassName = "org.postgresql.Driver"
        username = "admin"
        password = System.getenv("DB_PASSWORD") ?: "admin"
        maximumPoolSize = 10
    }

    Database.connect(HikariDataSource(config))

    log.info("Database connected successfully")
}

