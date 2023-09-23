package models

import kotlinx.serialization.Serializable

@Serializable
data class Bird(
    val author: String,
    val category: String,
    val path: String
)
