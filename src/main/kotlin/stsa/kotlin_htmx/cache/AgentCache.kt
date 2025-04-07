package stsa.kotlin_htmx.cache

import kotlinx.serialization.Serializable
import stsa.kotlin_htmx.api.models.Agent
import stsa.kotlin_htmx.api.models.Skin
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource


class AgentSearchCache {
    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private val timeSource = TimeSource.Monotonic

    data class CacheEntry(
        val agents: List<Agent>,
        val expirationTime: TimeSource.Monotonic.ValueTimeMark
    )

    // Tiempo de vida del caché (5 minutos)
    private val cacheTTL = 5.minutes

    fun get(key: String): List<Agent>? {
        val entry = cache[key] ?: return null
        if (entry.expirationTime.hasPassedNow()) {
            cache.remove(key)
            return null
        }
        return entry.agents
    }

    fun put(key: String, agents: List<Agent>) {
        cache[key] = CacheEntry(
            agents = agents,
            expirationTime = timeSource.markNow() + cacheTTL
        )
    }

    fun clear() {
        cache.clear()
    }

    fun size() = cache.size
}