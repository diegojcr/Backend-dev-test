package stsa.kotlin_htmx.controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import stsa.kotlin_htmx.api.models.ApiClientCS

class SearchControllerAG(private val apiClient: ApiClientCS) {
    fun configureRoutes(routing: Routing) {
        routing.route("/search") {
            get {
                val query = call.request.queryParameters["q"] ?: ""
                val agents = apiClient.searchAgents(query)
                call.respond(agents)
            }

            get("/filter") {
                val filters = ApiClientCS.AgentFilters(
                    searchQuery = call.request.queryParameters["q"], // Cambiado de searchText a q
                    teamFilter = call.request.queryParameters["team"]
                )
                val agents = apiClient.getFilteredAgents(filters)
                call.respond(agents)
            }
        }
    }
}