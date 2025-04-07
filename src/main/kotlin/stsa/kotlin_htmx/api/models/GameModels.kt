package stsa.kotlin_htmx.api.models

import kotlinx.serialization.Serializable

//modelo para skins
@Serializable
data class Skin(
    val id: String,
    val name: String,
    val description: String? = null,
    val team: Team,
    val crates: List<Crates> = emptyList(),
    val image: String? //aqui va a ir la URL de la imagen
)
//skins
@Serializable
data class Team(
    val name: String
)
//skins
@Serializable
data class Crates(
    val id: String
)

//Modelo para Agentes
@Serializable
data class Agent(
    val id: String,
    val name: String,
    val description: String? = null,
    val team: TeamAG,
    val image: String? = null
)
//Agentes
@Serializable
data class TeamAG(
    val name: String
)

//Modelo para crates
@Serializable
data class Crate(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null
)

//Modelo para keys
@Serializable
data class Key(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val crates: List<CratesK> = emptyList()
)

//Keys
@Serializable
data class CratesK(
    val id: String
)
