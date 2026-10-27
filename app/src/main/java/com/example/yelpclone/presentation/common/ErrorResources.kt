package com.example.yelpclone.presentation.common

import androidx.annotation.StringRes
import com.example.yelpclone.R
import com.example.yelpclone.presentation.common.model.ScreenError

/**
 * Maps screen errors to localized messages at the Android rendering boundary.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@StringRes
fun ScreenError.messageResource(): Int =
    when (this) {
        ScreenError.INVALID_LOCATION -> R.string.error_location
        ScreenError.NETWORK -> R.string.error_network
        ScreenError.SERVICE -> R.string.error_service
        ScreenError.INVALID_DATA -> R.string.error_data
        ScreenError.MISSING_CREDENTIALS -> R.string.error_credentials
    }
