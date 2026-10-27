package com.example.yelpclone.data.business.model

import com.google.gson.annotations.SerializedName

/**
 * Describes optional fields returned by the remote service before domain mapping.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class BusinessDto(
    val id: String? = null,
    val name: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    val rating: Double? = null,
    @SerializedName("review_count") val reviewCount: Int? = null,
    @SerializedName("display_phone") val displayPhone: String? = null,
    val phone: String? = null,
    val location: LocationDto? = null,
    val categories: List<CategoryDto>? = null,
    val distance: Double? = null,
    val coordinates: CoordinatesDto? = null,
)
