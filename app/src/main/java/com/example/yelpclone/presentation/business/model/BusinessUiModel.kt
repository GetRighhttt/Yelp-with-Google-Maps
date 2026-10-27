package com.example.yelpclone.presentation.business.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Contains display values and a bounded navigation payload for the feature screens.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@Parcelize
data class BusinessUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val phone: String,
    val address: String,
    val category: String,
    val distance: String,
    val latitude: Double?,
    val longitude: Double?,
) : Parcelable
