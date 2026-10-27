package com.example.yelpclone.presentation.business

import com.example.yelpclone.domain.business.model.Business
import com.example.yelpclone.presentation.business.model.BusinessUiModel
import java.util.Locale

/**
 * Prepares business values for cards, details, and map navigation.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
fun Business.toUiModel(): BusinessUiModel =
    BusinessUiModel(
        id = id,
        name = name,
        imageUrl = imageUrl,
        rating = rating.toFloat(),
        reviewCount = reviewCount,
        phone = phone,
        address = address,
        category = category,
        distance =
            distanceMeters
                ?.let {
                    String.format(Locale.getDefault(), "%.1f mi", it / 1609.344)
                }.orEmpty(),
        latitude = latitude,
        longitude = longitude,
    )
