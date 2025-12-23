package pro.ralan.services

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import pro.ralan.database.tables.OffersTable
import pro.ralan.models.CreateOfferRequest
import pro.ralan.models.Offer
import pro.ralan.models.UpdateOfferRequest
import java.util.*

object OfferService {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun createTable() {
        transaction {
            SchemaUtils.create(OffersTable)
        }
    }
    
    fun create(request: CreateOfferRequest): Offer {
        val newId = UUID.randomUUID()
        
        transaction {
            OffersTable.insert {
                it[id] = newId
                it[discipline] = json.encodeToString(request.discipline)
                it[offerName] = request.offerName
                it[date] = request.date
                it[price] = request.price
                it[time] = request.time
                it[level] = json.encodeToString(request.level)
            }
        }
        
        return Offer(
            id = newId.toString(),
            discipline = request.discipline,
            offerName = request.offerName,
            date = request.date,
            price = request.price,
            time = request.time,
            level = request.level
        )
    }
    
    fun getAll(): List<Offer> {
        return transaction {
            OffersTable.selectAll().map { row ->
                rowToOffer(row)
            }
        }
    }
    
    fun getById(id: String): Offer? {
        return try {
            val uuid = UUID.fromString(id)
            transaction {
                OffersTable.select(OffersTable.id eq uuid)
                    .map { rowToOffer(it) }
                    .singleOrNull()
            }
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    fun update(id: String, request: UpdateOfferRequest): Offer? {
        return try {
            val uuid = UUID.fromString(id)
            
            val existing = getById(id) ?: return null
            
            transaction {
                OffersTable.update({ OffersTable.id eq uuid }) { stmt ->
                    request.discipline?.let { disc -> stmt[discipline] = json.encodeToString(disc) }
                    request.offerName?.let { name -> stmt[offerName] = name }
                    request.date?.let { d -> stmt[date] = d }
                    request.price?.let { p -> stmt[price] = p }
                    request.time?.let { t -> stmt[time] = t }
                    request.level?.let { lvl -> stmt[level] = json.encodeToString(lvl) }
                }
            }
            
            // Возвращаем обновлённую запись
            Offer(
                id = id,
                discipline = request.discipline ?: existing.discipline,
                offerName = request.offerName ?: existing.offerName,
                date = request.date ?: existing.date,
                price = request.price ?: existing.price,
                time = request.time ?: existing.time,
                level = request.level ?: existing.level
            )
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    fun delete(id: String): Boolean {
        return try {
            val uuid = UUID.fromString(id)
            val deleted = transaction {
                OffersTable.deleteWhere { OffersTable.id eq uuid }
            }
            deleted > 0
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    private fun rowToOffer(row: ResultRow): Offer {
        return Offer(
            id = row[OffersTable.id].toString(),
            discipline = json.decodeFromString<List<String>>(row[OffersTable.discipline]),
            offerName = row[OffersTable.offerName],
            date = row[OffersTable.date],
            price = row[OffersTable.price],
            time = row[OffersTable.time],
            level = json.decodeFromString<List<String>>(row[OffersTable.level])
        )
    }
}
