package com.example.yelpclone.domain.business.model

/**
 * Defines the starting location and Yelp's supported location text length.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
object SearchLocation {
    const val DEFAULT = "Tampa, FL"
    const val MAX_LENGTH = 250

    fun isValid(value: String): Boolean = value.trim().length in 1..MAX_LENGTH
}
