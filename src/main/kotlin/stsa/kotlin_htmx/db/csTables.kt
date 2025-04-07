package stsa.kotlin_htmx.db

import org.jetbrains.exposed.sql.Table
import kotlin.math.max

object Skins : Table("Skins") {
    val id = varchar("id", 800)
    val name = varchar("name", 800)
    val description = varchar("description", 800).nullable()
    val image = varchar("image", 800).nullable()
    val team = varchar("team", 800)
    val crates = varchar("crates", 800)
}

object Agents : Table("agents") {
    val id = varchar("id", 800)
    val name = varchar("name", 999)
    val description = varchar("description", 999).nullable()
    val image = varchar("image", 999).nullable()
    val team = varchar("team", 999)
}

object Crates : Table("crates") {
    val id = varchar("id", 900)
    val name = varchar("name", 950)
    val description = varchar("description", 950).nullable()
    val image = varchar("image", 950).nullable()
}

object Keys : Table("keys") {
    val id = varchar("id", 900)
    val name = varchar("name", 900)
    val description = varchar("description", 900).nullable()
    val image = varchar("image", 900).nullable()
    val crates = varchar("crates", 900)
}