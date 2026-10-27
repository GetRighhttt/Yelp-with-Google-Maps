package com.example.yelpclone.presentation.common

import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.presentation.common.model.ScreenError

/**
 * Translates domain failures into presentation-owned error values.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
fun FailureReason.toScreenError(): ScreenError =
    when (this) {
        FailureReason.INVALID_LOCATION -> ScreenError.INVALID_LOCATION
        FailureReason.NETWORK -> ScreenError.NETWORK
        FailureReason.SERVICE -> ScreenError.SERVICE
        FailureReason.INVALID_DATA -> ScreenError.INVALID_DATA
        FailureReason.MISSING_CREDENTIALS -> ScreenError.MISSING_CREDENTIALS
    }
