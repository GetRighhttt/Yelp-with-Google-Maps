package com.example.yelpclone.domain.common

/**
 * Identifies failures that presentation can translate into user-facing messages.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
enum class FailureReason { NETWORK, SERVICE, INVALID_DATA, MISSING_CREDENTIALS, INVALID_LOCATION }
