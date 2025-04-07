package stsa.kotlin_htmx.auth
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import stsa.kotlin_htmx.UserSession
import java.util.*
import stsa.kotlin_htmx.auth.UUIDSerializer

fun Application.configureAuthentication() {
    //val sessionVersion = UUID.randomUUID()
    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if (session.username == AuthController.VALID_USERNAME) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/auth/login")
            }
        }
    }
}