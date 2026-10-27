package com.example.yelpclone.data.business.model

/**
 * Describes optional fields returned by the remote service before domain mapping.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class BusinessResponseDto(
    val businesses: List<BusinessDto>? = null,
)
