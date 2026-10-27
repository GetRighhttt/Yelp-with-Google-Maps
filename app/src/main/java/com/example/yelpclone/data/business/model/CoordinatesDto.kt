package com.example.yelpclone.data.business.model

/**
 * Describes optional fields returned by the remote service before domain mapping.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class CoordinatesDto(
    val latitude: Double? = null,
    val longitude: Double? = null,
)
