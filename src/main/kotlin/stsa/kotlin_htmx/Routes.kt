package stsa.kotlin_htmx

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import stsa.kotlin_htmx.pages.*
import stsa.kotlin_htmx.link.pages.LinkMainPage
import org.slf4j.LoggerFactory
import stsa.kotlin_htmx.agents.pages.AgentsPage
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.auth.AuthController
import stsa.kotlin_htmx.auth.configureAuthentication
import stsa.kotlin_htmx.crates.pages.CratesPage
import stsa.kotlin_htmx.db.Skins
import stsa.kotlin_htmx.keys.pages.KeysPage
import stsa.kotlin_htmx.skins.pages.SkinsPage
import stsa.kotlin_htmx.auth.UUIDSerializer


private val logger = LoggerFactory.getLogger("stsa.kotlin_htmx.Routes")

fun Application.configurePageRoutes() {
    val authController = AuthController()
    routing {
        authController.configureAuthRoutes(this)
        get {
            call.respondHtmlTemplate(MainTemplate(template = EmptyTemplate(), "Front page")) {
                mainSectionTemplate {
                    emptyContentWrapper {
                        section {
                            p {
                                +"Startrack Demo"
                            }
                        }
                    }
                }
            }
        }

        //Ruta para mostrar las skins
        route("/skins") {
            val skinsPage = SkinsPage()
            val apiClient = ApiClientCS()
            get {
                skinsPage.renderMainPage(this)

            }
            get("/search") {
                skinsPage.renderMainPage(this)
            }
        }

        //Ruta para mostrar los agentes
        route("/agents") {
            val agentsPage = AgentsPage()
            get {
                agentsPage.renderMainPage(this)
            }
            get("/search") {
                agentsPage.renderMainPage(this)
            }
        }

        //Ruta para mostrar los crates
        route("/crates") {
            val cratesPage = CratesPage()
            get {
                cratesPage.renderMainPage(this)
            }
            get("/search") {
                cratesPage.renderMainPage(this)
            }
        }

        //Ruta protegida
        authenticate("auth-session") {

            //Ruta para mostrar las keys
            route("/keys") {
                val keysPage = KeysPage()
                get {
                    keysPage.renderMainPage(this)
                }
                get("/search") {
                    keysPage.renderMainPage(this)
                }
            }
        }

        route("/link") {
            val linkMainPage = LinkMainPage()
            get {
                linkMainPage.renderMainPage(this)
            }
        }


    }
}
