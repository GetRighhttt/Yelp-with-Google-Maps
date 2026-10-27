package com.example.yelpclone.data.business.model

import com.google.gson.annotations.SerializedName

/**
 * Describes optional fields returned by the remote service before domain mapping.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class LocationDto(
    @SerializedName("display_address") val displayAddress: List<String>? = null,
    val address1: String? = null,
    val city: String? = null,
    val state: String? = null,
    @SerializedName("zip_code") val zipCode: String? = null,
)
