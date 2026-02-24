package pro.ralan.database.tables

import org.jetbrains.exposed.sql.Table

object OffersTable : Table("offers") {
    val id = uuid("id")
    val discipline = text("discipline") // Хранится как JSON массив
    val offerName = varchar("offer_name", 255)
    val date = varchar("date", 50)
    val price = integer("price")
    val time = varchar("time", 50)
    val level = text("level") // Хранится как JSON массив
    val important = bool("important").default(false)
    
    override val primaryKey = PrimaryKey(id)
}

