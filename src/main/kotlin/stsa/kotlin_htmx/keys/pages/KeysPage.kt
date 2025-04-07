package stsa.kotlin_htmx.keys.pages

import io.ktor.server.html.*
import io.ktor.server.routing.RoutingContext
import kotlinx.html.*
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.pages.MainTemplate
import stsa.kotlin_htmx.pages.SelectionTemplate
import org.slf4j.LoggerFactory
import stsa.kotlin_htmx.skins.pages.SkinsPage

class KeysPage {
    private val logger = LoggerFactory.getLogger(SkinsPage::class.java)
    private val apiClient = ApiClientCS()

    suspend fun renderMainPage(routingHandler: RoutingContext) {
        with(routingHandler) {
            try {
                val query = call.request.queryParameters["query"]
                val keys = if (query.isNullOrEmpty()) {
                    apiClient.getAllKeys()
                } else {
                    apiClient.searchKeys(query)
                }
                //val keys = apiClient.getAllKeys()
                call.respondHtmlTemplate(MainTemplate(template = SelectionTemplate(), "CSGO Keys")) {
                    mainSectionTemplate {
                        selectionPagesContent {
                            searchBarK()
                            section {
                                div("skins-grid") {
                                    id = "skins-container"
                                    keys.forEach { key ->
                                        div("skin-card") {
                                            style = "text-align: center;"

                                            h3 { +key.id}
                                            h3 { +key.name }
                                            p { +"Description: ${key.description}" }
                                            p { +"Image URL: ${key.image}" ?: "" }
                                            // Mostrar los crate IDs si existen
                                            if (key.crates.isNotEmpty()) {
                                                div("crates-list") {
                                                    style = "margin-top: 0.5rem; font-size: 0.8rem;"
                                                    +"Crates: "
                                                    key.crates.forEachIndexed { index, crate ->
                                                        if (index > 0) span { +", " }
                                                        span { +crate.id }
                                                    }
                                                }
                                            }

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
    private fun FlowContent.searchBarK() {
        div("search-container") {
            form {
                attributes["hx-get"] = "/keys/search"
                attributes["hx-target"] = "#skins-container"
                attributes["hx-trigger"] = "keyup changed delay:500ms, submit"

                input {
                    type = InputType.text
                    name = "query"
                    placeholder = "Buscar keys por nombre o por crate"
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