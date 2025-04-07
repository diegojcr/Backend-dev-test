package stsa.kotlin_htmx.auth
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import stsa.kotlin_htmx.UserSession
import stsa.kotlin_htmx.auth.UUIDSerializer


class AuthController {
    companion object {
        const val VALID_USERNAME = "admin123"
        const val VALID_PASSWORD = "clave123"
    }
    fun configureAuthRoutes(routing: Routing) {
        routing.route("/auth") {
            get("/login") {
                call.respondHtml {
                    head {
                        title { +"Login" }
                    }
                    body {
                        form(action = "/auth/login", method = FormMethod.post) {
                            div {
                                label { +"Usuario:" }
                                input(type = InputType.text, name = "username") {
                                    required = true
                                }
                            }
                            div {
                                label { +"Contraseña:" }
                                input(type = InputType.password, name = "password") {
                                    required = true
                                }
                            }
                            button(type = ButtonType.submit) { +"Ingresar" }
                        }
                    }
                }
            }

            post("/login") {
                val params = call.receiveParameters()
                val username = params["username"] ?: ""
                val password = params["password"] ?: ""

                if (username == VALID_USERNAME && password == VALID_PASSWORD) {
                    call.sessions.set(UserSession(username))
                    call.respondRedirect("/keys")
                } else {
                    call.respondRedirect("/auth/login?error=invalid_credentials")
                }
            }

            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
    }
}