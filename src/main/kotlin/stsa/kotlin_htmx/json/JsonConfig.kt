package stsa.kotlin_htmx.json

import kotlinx.serialization.json.Json


class JsonConfig {
    val csgoJson = Json {
        ignoreUnknownKeys = true  // Ignora campos no mapeados
        explicitNulls = false    // Omite campos nulos
        prettyPrint = true       // Solo para debugging
    }
}