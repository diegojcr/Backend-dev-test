package stsa.kotlin_htmx.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import stsa.kotlin_htmx.api.models.ApiClientCS

class SearchControllerCR(private val apiClient: ApiClientCS) {
    fun configureRoutes(routing: Routing) {
        routing.route("/search") {
            get {
                val query = call.request.queryParameters["q"] ?: ""
                val crates = apiClient.searchCrates(query)
                call.respond(crates)
            }

            get("/filter") {
                val filters = ApiClientCS.CrateFilters(
                    searchQuery = call.request.queryParameters["q"], // Cambiado de searchText a q
                    teamFilter = call.request.queryParameters["team"]
                )
                val crates = apiClient.getFilteredCrates(filters)
                call.respond(crates)
            }
        }
    }
}