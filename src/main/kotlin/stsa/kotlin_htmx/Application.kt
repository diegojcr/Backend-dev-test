package stsa.kotlin_htmx

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.launch
import stsa.kotlin_htmx.plugins.configureHTTP
import stsa.kotlin_htmx.plugins.configureMonitoring
import stsa.kotlin_htmx.plugins.configureRouting
import stsa.kotlin_htmx.services.DataService
import java.io.File
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.auth.configureAuthentication
import stsa.kotlin_htmx.db.*
import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import stsa.kotlin_htmx.auth.UUIDSerializer


data class ApplicationConfig(
    val lookupApiKey: String
) {

    companion object {
        fun load(): ApplicationConfig {
            System.setProperty("io.ktor.development", "true")

            fun Map<String, String>.envOrLookup(key: String): String {
                return System.getenv(key) ?: this[key]!!
            }

            val envVars: Map<String, String> = envFile().let { envFile ->
                if (envFile.exists()) {
                    envFile.readLines()
                        .map { it.split("=") }
                        .filter { it.size == 2 }
                        .associate { it.first().trim() to it.last().trim() }
                } else emptyMap()
            }

            return ApplicationConfig(
                lookupApiKey = envVars.envOrLookup("LOOKUP_API_KEY")
            )
        }

    }
}

fun envFile(): File {
    // I don't really recommend having this default env file, but do it now to ease testing of example app
    // Settings in ENV will override file always
    return listOf(".env.local", ".env.default").map { File(it) }.first { it.exists() }
}

fun main() {
    // Have to do this before the rest of the loading of KTor. I guess it's because it does something fancy
    // with the classloader to be able to do hot reload.
    if (envFile().readText().contains("KTOR_DEVELOPMENT=true")) System.setProperty(
        "io.ktor.development",
        "true"
    )
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}
@Serializable
data class UserSession(
    val username: String
)
fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureRouting()
    configureAuthentication()
    install(Compression)
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = SerializersModule {
                    contextual(UUID::class, UUIDSerializer)
                }
                encodeDefaults = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 3600 // 1 hora de sesión
        }
    }


    // Manual dependency injection :) Usually smart to find a separate place to do this from KTor
    val config = ApplicationConfig.load()

    //inicializar bd y servicios
    initDatabase()
    val csService = DataService()

    //cargar datos cuando la app se inicia
    launch {
        csService.loadSkins()
        csService.loadAgents()
        csService.loadCrates()
        csService.loadKeys()
    }
    // Hook que se ejecuta al cerrar la app
    environment.monitor.subscribe(ApplicationStopped) {
        log.info("🧹 Limpiando base de datos al cerrar el servidor...")

        transaction {
            Keys.deleteAll()
            Skins.deleteAll()
            Agents.deleteAll()
            Crates.deleteAll()
        }

        log.info("✅ Tablas limpiadas con éxito.")
    }

    // Load pages
    configurePageRoutes()

}
