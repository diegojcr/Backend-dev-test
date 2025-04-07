package stsa.kotlin_htmx.crates.pages

import io.ktor.server.html.*
import io.ktor.server.routing.RoutingContext
import kotlinx.html.*
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.pages.MainTemplate
import stsa.kotlin_htmx.pages.SelectionTemplate
import org.slf4j.LoggerFactory
import stsa.kotlin_htmx.skins.pages.SkinsPage

class CratesPage {
    private val logger = LoggerFactory.getLogger(SkinsPage::class.java)
    private val apiClient = ApiClientCS()

    suspend fun renderMainPage(routingHandler: RoutingContext) {
        with(routingHandler) {
            try {
                val query = call.request.queryParameters["query"]
                val crates = if (query.isNullOrEmpty()) {
                    apiClient.getAllCrates()
                } else {
                    apiClient.searchCrates(query)
                }
                //val crates = apiClient.getAllCrates()
                call.respondHtmlTemplate(MainTemplate(template = SelectionTemplate(), "CSGO Crates")) {
                    mainSectionTemplate {
                        selectionPagesContent {
                            searchBarCR()
                            section {
                                div("skins-grid") {
                                    id = "skins-container"
                                    crates.forEach { crate ->
                                        div("skin-card") {
                                            style = "text-align: center;"

                                            h3 { +crate.id}
                                            h3 { +crate.name }
                                            p { +"Description: ${crate.description}" }
                                            p { +"Image URL: ${crate.image}" ?: "" }


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

    private fun FlowContent.searchBarCR() {
        div("search-container") {
            form {
                attributes["hx-get"] = "/crates/search"
                attributes["hx-target"] = "#skins-container"
                attributes["hx-trigger"] = "keyup changed delay:500ms, submit"

                input {
                    type = InputType.text
                    name = "query"
                    placeholder = "Buscar crates por nombre"
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