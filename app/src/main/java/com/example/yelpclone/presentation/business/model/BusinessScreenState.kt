package com.example.yelpclone.presentation.business.model

import com.example.yelpclone.presentation.common.model.ScreenError

/**
 * Keeps request input, progress, results, and persistent errors in one observable value.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
data class BusinessScreenState(
    val location: String,
    val query: String = "",
    val locationDraft: String = location,
    val isEditingLocation: Boolean = false,
    val locationError: ScreenError? = null,
    val isLoading: Boolean = false,
    val items: List<BusinessUiModel> = emptyList(),
    val error: ScreenError? = null,
)
