package stsa.kotlin_htmx.agents.pages

import io.ktor.server.html.*
import io.ktor.server.routing.RoutingContext
import kotlinx.html.*
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.pages.MainTemplate
import stsa.kotlin_htmx.pages.SelectionTemplate
import org.slf4j.LoggerFactory
import stsa.kotlin_htmx.skins.pages.SkinsPage

class AgentsPage {

    private val logger = LoggerFactory.getLogger(SkinsPage::class.java)
    private val apiClient = ApiClientCS()

    suspend fun renderMainPage(routingHandler: RoutingContext) {
        with(routingHandler) {
            try {
                val query = call.request.queryParameters["query"]
                val agents = if (query.isNullOrEmpty()) {
                    apiClient.getAllAgents()
                } else {
                    apiClient.searchAgents(query)
                }
                //val agents = apiClient.getAllAgents()
                call.respondHtmlTemplate(MainTemplate(template = SelectionTemplate(), "CSGO Agents")) {
                    mainSectionTemplate {
                        selectionPagesContent {
                            searchBarAG()
                            section {
                                div("skins-grid") {
                                    id = "skins-container"
                                    agents.forEach { agent ->
                                        div("skin-card") {
                                            style = "text-align: center;"

                                            h3 { +agent.id}
                                            h3 { +agent.name }
                                            p { +"Description: ${agent.description}" }
                                            p { +"Image URL: ${agent.image}" ?: "" }
                                            p { +"Team: ${agent.team.name}" }


                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Error loading skins", e)
                call.respondHtmlTemplate(MainTemplate(template = SelectionTemplate(), "Error")) {
                    mainSectionTemplate {
                        selectionPagesContent {
                            section {
                                div("box") {
                                    style = "color: red;"
                                    +"Error al cargar las skins: ${e.message}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.searchBarAG() {
        div("search-container") {
            form {
                attributes["hx-get"] = "/agents/search"
                attributes["hx-target"] = "#skins-container"
                attributes["hx-trigger"] = "keyup changed delay:500ms, submit"

                input {
                    type = InputType.text
                    name = "query"
                    placeholder = "Buscar agentes por nombre, o equipo"
                    attributes["aria-label"] = "Buscar skins"
                }

                button {
                    type = ButtonType.submit
                    +"Buscar"
                }
            }

            // Indicador de carga
            div("htmx-indicator loading-spinner") {
                +"Buscando..."
            }
        }
    }

}