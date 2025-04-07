package stsa.kotlin_htmx.services

import stsa.kotlin_htmx.api.models.ApiClientCS
import stsa.kotlin_htmx.db.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class DataService {
    private val apiClient = ApiClientCS()

    suspend fun loadSkins(){
        val skins = apiClient.getAllSkins()
        transaction {
            skins.forEach { skin ->
                Skins.insert {
                    it[id] = skin.id
                    it[name] = skin.name
                    it[description] = skin.description
                    it[image] = skin.image
                    it[team] = skin.team.name

                }
            }
        }
    }
    suspend fun loadAgents(){
        val agents = apiClient.getAllAgents()
        transaction {
            agents.forEach { agent ->
                Agents.insert {
                    it[id] = agent.id
                    it[name] = agent.name
                    it[description] = agent.description
                    it[image] = agent.image
                    it[team] = agent.team.name

                }
            }
        }
    }
    suspend fun loadCrates(){
        val crates = apiClient.getAllCrates()
        transaction {
            crates.forEach { crate ->
                Crates.insert {
                    it[id] = crate.id
                    it[name] = crate.name
                    it[description] = crate.description
                    it[image] = crate.image

                }
            }
        }
    }
    suspend fun loadKeys(){
        val keys = apiClient.getAllKeys()
        transaction {
            keys.forEach { key ->
                Keys.insert {
                    it[id] = key.id
                    it[name] = key.name
                    it[description] = key.description
                    it[image] = key.image

                }
            }
        }
    }
}