package com.rktuhin.shopflow.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("slug") val slug: String,
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
