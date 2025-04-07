package stsa.kotlin_htmx.api.models

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.css.a
import kotlinx.serialization.json.Json
import stsa.kotlin_htmx.api.models.*
import stsa.kotlin_htmx.cache.AgentSearchCache
import stsa.kotlin_htmx.cache.CrateSearchCache
import stsa.kotlin_htmx.cache.KeySearchCache
import stsa.kotlin_htmx.cache.SkinSearchCache
import java.util.*


val csgoJson = Json {
    ignoreUnknownKeys = true  // Ignora campos no mapeados
    explicitNulls = false    // Omite campos nulos
    prettyPrint = true       // Solo para debugging
}


class ApiClientCS {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json{
                ignoreUnknownKeys = true
                prettyPrint = true
                explicitNulls = false
            })
        }

    }

    // Clase para filtros simplificada
    data class SkinFilters(
        val searchQuery: String? = null,
        val teamFilter: String? = null,
        val hasImage: Boolean = false
    )
    // Clase para filtros simplificada
    data class AgentFilters(
        val searchQuery: String? = null,
        val teamFilter: String? = null,
        val hasImage: Boolean = false
    )
    // Clase para filtros simplificada
    data class CrateFilters(
        val searchQuery: String? = null,
        val teamFilter: String? = null,
        val hasImage: Boolean = false
    )
    // Clase para filtros simplificada
    data class KeyFilters(
        val searchQuery: String? = null,
        val teamFilter: String? = null,
        val hasImage: Boolean = false
    )

    private val searchCache = SkinSearchCache()
    private val cacheMutex = Mutex()
    private val searchCacheAG = AgentSearchCache()
    private val cacheMutexAG = Mutex()
    private val searchCacheCR = CrateSearchCache()
    private val cacheMutexCR = Mutex()
    private val searchCacheK = KeySearchCache()
    private val cacheMutexK = Mutex()

    private val baseURL = "https://raw.githubusercontent.com/ByMykel/CSGO-API/main/public/api/en"

    // Cachear todas las skins al iniciar
    private var allSkinsCache: List<Skin>? = null
    // Cachear todos los agentes al iniciar
    private var allAgentsCache: List<Agent>? = null
    // Cachear todos los crates al iniciar
    private var allCratesCache: List<Crate>? = null
    // Cachear todos los keys al iniciar
    private var allKeysCache: List<Key>? = null

    suspend fun getAllSkins(): List<Skin>{
        return allSkinsCache ?: run {
            val response: HttpResponse = client.get("$baseURL/skins.json")
            val jsonString = response.bodyAsText() // Leer como texto primero

            try {
                csgoJson.decodeFromString<List<Skin>>(jsonString).also {

                    allSkinsCache = it
                }
            } catch (e: Exception) {
                throw RuntimeException("Error parsing skins JSON: ${e.message}", e)
            }
        }
    }

    suspend fun searchSkins(query: String): List<Skin> {
        val cacheKey = "search:${query.lowercase(Locale.getDefault())}"

        return cacheMutex.withLock {
            searchCache.get(cacheKey) ?: run {
                val allSkins = getAllSkins()
                val results = allSkins.filter { skin ->
                    query.contains(skin.name, ignoreCase = true) ||
                            skin.name.contains(query, ignoreCase = true) ||
                            skin.description?.contains(query, ignoreCase = true) ?: false ||
                            skin.team.name.contains(query, ignoreCase = true) ||
                            skin.crates.any { crate ->
                                crate.id.contains(query, ignoreCase = true)
                            }
                }
                searchCache.put(cacheKey, results)
                results
            }
        }
    }
    suspend fun getFilteredSkins(filters: SkinFilters): List<Skin> {
        val cacheKey = "filters:" +
                "${filters.searchQuery?.lowercase(Locale.getDefault())}:" +
                "${filters.teamFilter?.lowercase(Locale.getDefault())}:" +
                "${filters.hasImage}"

        return cacheMutex.withLock {
            searchCache.get(cacheKey) ?: run {
                val allSkins = getAllSkins()
                val results = allSkins.filter { skin ->
                    (filters.searchQuery.isNullOrEmpty() ||
                            skin.name.contains(filters.searchQuery, ignoreCase = true) ||
                            skin.description?.contains(filters.searchQuery, ignoreCase = true) ?: false ||
                            skin.team.name.contains(filters.searchQuery, ignoreCase = true) ||
                            skin.crates.any { it.id.contains(filters.searchQuery, ignoreCase = true) }) &&
                            (filters.teamFilter.isNullOrEmpty() ||
                                    skin.team.name.equals(filters.teamFilter, ignoreCase = true)) &&
                            (!filters.hasImage || !skin.image.isNullOrEmpty())
                }
                searchCache.put(cacheKey, results)
                results
            }
        }
    }


    suspend fun getAllAgents(): List<Agent>{
        return allAgentsCache ?: run {
            val response: HttpResponse = client.get("$baseURL/agents.json")
            val jsonString = response.bodyAsText() // Leer como texto primero

            try {
                csgoJson.decodeFromString<List<Agent>>(jsonString).also {

                    allAgentsCache = it
                }
            } catch (e: Exception) {
                throw RuntimeException("Error parsing skins JSON: ${e.message}", e)
            }
        }
    }
    suspend fun searchAgents(query: String): List<Agent> {
        val cacheKey = "search:${query.lowercase(Locale.getDefault())}"

        return cacheMutexAG.withLock {
            searchCacheAG.get(cacheKey) ?: run {
                val allAgents = getAllAgents()
                val results = allAgents.filter { agent ->
                    query.contains(agent.name, ignoreCase = true) ||
                            agent.name.contains(query, ignoreCase = true) ||
                            agent.description?.contains(query, ignoreCase = true) ?: false ||
                            agent.team.name.contains(query, ignoreCase = true)

                }
                searchCacheAG.put(cacheKey, results)
                results
            }
        }
    }
    suspend fun getFilteredAgents(filters: AgentFilters): List<Agent> {
        val cacheKey = "filters:" +
                "${filters.searchQuery?.lowercase(Locale.getDefault())}:" +
                "${filters.teamFilter?.lowercase(Locale.getDefault())}:" +
                "${filters.hasImage}"

        return cacheMutexAG.withLock {
            searchCacheAG.get(cacheKey) ?: run {
                val allAgents = getAllAgents()
                val results = allAgents.filter { agent ->
                    (filters.searchQuery.isNullOrEmpty() ||
                            agent.name.contains(filters.searchQuery, ignoreCase = true) ||
                            agent.description?.contains(filters.searchQuery, ignoreCase = true) ?: false ||
                            agent.team.name.contains(filters.searchQuery, ignoreCase = true) ||
                                    agent.team.name.equals(filters.teamFilter, ignoreCase = true))
                }
                searchCacheAG.put(cacheKey, results)
                results
            }
        }
    }

    suspend fun getAllCrates(): List<Crate>{
        return allCratesCache ?: run {
            val response: HttpResponse = client.get("$baseURL/crates.json")
            val jsonString = response.bodyAsText() // Leer como texto primero

            try {
                csgoJson.decodeFromString<List<Crate>>(jsonString).also {

                    allCratesCache = it
                }
            } catch (e: Exception) {
                throw RuntimeException("Error parsing skins JSON: ${e.message}", e)
            }
        }
    }
    suspend fun searchCrates(query: String): List<Crate> {
        val cacheKey = "search:${query.lowercase(Locale.getDefault())}"

        return cacheMutexCR.withLock {
            searchCacheCR.get(cacheKey) ?: run {
                val allCrates = getAllCrates()
                val results = allCrates.filter { crate ->
                    query.contains(crate.name, ignoreCase = true) ||
                            crate.name.contains(query, ignoreCase = true) ||
                            crate.description?.contains(query, ignoreCase = true) ?: false

                }
                searchCacheCR.put(cacheKey, results)
                results
            }
        }
    }
    suspend fun getFilteredCrates(filters: CrateFilters): List<Crate> {
        val cacheKey = "filters:" +
                "${filters.searchQuery?.lowercase(Locale.getDefault())}:" +
                "${filters.teamFilter?.lowercase(Locale.getDefault())}:" +
                "${filters.hasImage}"

        return cacheMutexCR.withLock {
            searchCacheCR.get(cacheKey) ?: run {
                val allCrates = getAllCrates()
                val results = allCrates.filter { crate ->
                    (filters.searchQuery.isNullOrEmpty() ||
                            crate.name.contains(filters.searchQuery, ignoreCase = true) ||
                            crate.description?.contains(filters.searchQuery, ignoreCase = true) ?: false)
                }
                searchCacheCR.put(cacheKey, results)
                results
            }
        }
    }



    suspend fun getAllKeys(): List<Key>{
        return allKeysCache ?: run {
            val response: HttpResponse = client.get("$baseURL/keys.json")
            val jsonString = response.bodyAsText() // Leer como texto primero

            try {
                csgoJson.decodeFromString<List<Key>>(jsonString).also {

                    allKeysCache = it
                }
            } catch (e: Exception) {
                throw RuntimeException("Error parsing skins JSON: ${e.message}", e)
            }
        }
    }
    suspend fun searchKeys(query: String): List<Key> {
        val cacheKey = "search:${query.lowercase(Locale.getDefault())}"

        return cacheMutexK.withLock {
            searchCacheK.get(cacheKey) ?: run {
                val allKeys = getAllKeys()
                val results = allKeys.filter { key ->
                    query.contains(key.name, ignoreCase = true) ||
                            key.name.contains(query, ignoreCase = true) ||
                            key.description?.contains(query, ignoreCase = true) ?: false

                }
                searchCacheK.put(cacheKey, results)
                results
            }
        }
    }
    suspend fun getFilteredKeys(filters: KeyFilters): List<Key> {
        val cacheKey = "filters:" +
                "${filters.searchQuery?.lowercase(Locale.getDefault())}:" +
                "${filters.teamFilter?.lowercase(Locale.getDefault())}:" +
                "${filters.hasImage}"

        return cacheMutexK.withLock {
            searchCacheK.get(cacheKey) ?: run {
                val allKeys = getAllKeys()
                val results = allKeys.filter { key ->
                    (filters.searchQuery.isNullOrEmpty() ||
                            key.name.contains(filters.searchQuery, ignoreCase = true) ||
                            key.description?.contains(filters.searchQuery, ignoreCase = true) ?: false)
                }
                searchCacheK.put(cacheKey, results)
                results
            }
        }
    }



}