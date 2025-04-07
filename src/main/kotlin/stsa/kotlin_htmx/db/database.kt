package stsa.kotlin_htmx.db

import org.jetbrains.exposed.sql.Database




fun initDatabase(){
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/counterstrike",
        user = "postgres",
        password = "123456",
        driver = "org.postgresql.Driver"
    )
}