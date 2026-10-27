package com.example.yelpclone.data.business.mapper

import com.example.yelpclone.data.business.model.BusinessDto
import com.example.yelpclone.domain.business.model.Business

/**
 * Converts Yelp records into validated business values at the repository boundary.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
fun BusinessDto.toDomain(): Business =
    Business(
        id = requireNotNull(id).also { require(it.isNotBlank()) },
        name = requireNotNull(name).also { require(it.isNotBlank()) },
        imageUrl = imageUrl.orEmpty(),
        rating = rating?.takeIf { it.isFinite() }?.coerceIn(0.0, 5.0) ?: 0.0,
        reviewCount = reviewCount?.coerceAtLeast(0) ?: 0,
        phone = displayPhone?.takeIf { it.isNotBlank() } ?: phone.orEmpty(),
        address =
            location
                ?.let { location ->
                    (
                        location.displayAddress
                            ?: listOfNotNull(location.address1, location.city, location.state, location.zipCode)
                    ).filter { it.isNotBlank() }
                        .joinToString(", ")
                }.orEmpty(),
        category = categories.orEmpty().mapNotNull { it.title }.joinToString(" · "),
        distanceMeters = distance?.takeIf { it.isFinite() && it >= 0 },
        latitude = coordinates?.latitude?.takeIf { it.isFinite() && it in -90.0..90.0 },
        longitude = coordinates?.longitude?.takeIf { it.isFinite() && it in -180.0..180.0 },
    )
