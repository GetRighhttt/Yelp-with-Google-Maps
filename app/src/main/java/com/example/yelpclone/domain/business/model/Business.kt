package com.example.yelpclone.domain.business.model

/**
 * Contains business values used by discovery, independent of Android and Yelp JSON.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class Business(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val phone: String,
    val address: String,
    val category: String,
    val distanceMeters: Double?,
    val latitude: Double?,
    val longitude: Double?,
)
