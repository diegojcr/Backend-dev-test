package stsa.kotlin_htmx.skins.pages

import io.ktor.server.html.*
import io.ktor.server.routing.RoutingContext
import kotlinx.html.*
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.pages.MainTemplate
import stsa.kotlin_htmx.pages.SelectionTemplate
import org.slf4j.LoggerFactory

class SkinsPage {
    private val logger = LoggerFactory.getLogger(SkinsPage::class.java)
    private val apiClient = ApiClientCS()

    suspend fun renderMainPage(routingHandler: RoutingContext) {
        with(routingHandler) {
            try {
                val query = call.request.queryParameters["query"]
                val skins = if (query.isNullOrEmpty()) {
                    apiClient.getAllSkins()
                } else {
                    apiClient.searchSkins(query)
                }
                //val skins = apiClient.getAllSkins()
                call.respondHtmlTemplate(MainTemplate(template = SelectionTemplate(), "CSGO Skins")) {
                    mainSectionTemplate {
                        selectionPagesContent {
                            searchBar()
                            section {
                                div("skins-grid") {
                                    id = "skins-container"
                                    skins.forEach { skin ->
                                        div("skin-card") {
                                            style = "text-align: center;"

                                            h3 { +skin.id}
                                            h3 { +skin.name }
                                            p { +"Description: ${skin.description}" }
                                            p { +"Image URL: ${skin.image}" ?: "" }
                                            p { +"Team: ${skin.team.name}" }
                                            // Mostrar los crate IDs si existen
                                            if (skin.crates.isNotEmpty()) {
                                                div("crates-list") {
                                                    style = "margin-top: 0.5rem; font-size: 0.8rem;"
                                                    +"Crates: "
                                                    skin.crates.forEachIndexed { index, crate ->
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



    private fun FlowContent.searchBar() {
        div("search-container") {
            form {
                attributes["hx-get"] = "/skins/search"
                attributes["hx-target"] = "#skins-container"
                attributes["hx-trigger"] = "keyup changed delay:500ms, submit"

                input {
                    type = InputType.text
                    name = "query"
                    placeholder = "Buscar skins por nombre, equipo o crate..."
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