package stsa.kotlin_htmx.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.api.models.ApiClientCS.SkinFilters

class SearchController(private val apiClient: ApiClientCS) {
    fun configureRoutes(routing: Routing) {
        routing.route("/search") {
            get {
                val query = call.request.queryParameters["q"] ?: ""
                val skins = apiClient.searchSkins(query)
                call.respond(skins)
            }

            get("/filter") {
                val filters = ApiClientCS.SkinFilters(
                    searchQuery = call.request.queryParameters["q"], // Cambiado de searchText a q
                    teamFilter = call.request.queryParameters["team"]
                )
                val skins = apiClient.getFilteredSkins(filters)
                call.respond(skins)
            }
        }
    }
}