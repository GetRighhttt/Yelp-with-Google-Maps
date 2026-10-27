package com.example.yelpclone

import com.example.yelpclone.di.AppModule
import com.example.yelpclone.domain.business.model.Business
import com.example.yelpclone.domain.business.repository.BusinessRepository
import com.example.yelpclone.domain.common.FailureReason
import com.example.yelpclone.domain.common.RepositoryResult
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Supplies deterministic device-test data without contacting live APIs or using credentials.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
object FixtureModule {
    @Provides fun businesses(): BusinessRepository =
        object : BusinessRepository {
            override suspend fun searchBusinesses(
                term: String,
                location: String,
            ): RepositoryResult<List<Business>> =
                when (term) {
                    "offline" -> {
                        RepositoryResult.Failure(FailureReason.NETWORK)
                    }

                    "nothing" -> {
                        RepositoryResult.Success(emptyList())
                    }

                    else -> {
                        RepositoryResult.Success(
                            listOf(
                                Business(
                                    id = "coffee",
                                    name = "The Neighborhood Cafe",
                                    imageUrl = "",
                                    rating = 4.5,
                                    reviewCount = 128,
                                    phone = "813-555-0100",
                                    address = "120 Franklin St, Tampa, FL",
                                    category = "Coffee & tea",
                                    distanceMeters = 800.0,
                                    latitude = 27.9506,
                                    longitude = -82.4572,
                                ),
                                Business(
                                    id = "brunch",
                                    name = "Sunday Table",
                                    imageUrl = "",
                                    rating = 4.7,
                                    reviewCount = 86,
                                    phone = "813-555-0101",
                                    address = "420 N Tampa St, Tampa, FL",
                                    category = "Breakfast & brunch",
                                    distanceMeters = 1400.0,
                                    latitude = 27.951,
                                    longitude = -82.458,
                                ),
                                Business(
                                    id = "pizza",
                                    name = "Bayside Pizza",
                                    imageUrl = "",
                                    rating = 4.4,
                                    reviewCount = 242,
                                    phone = "813-555-0102",
                                    address = "810 Water St, Tampa, FL",
                                    category = "Pizza · Italian",
                                    distanceMeters = 2100.0,
                                    latitude = 27.943,
                                    longitude = -82.451,
                                ),
                            ).map { business ->
                                if (location == "Tampa, FL") {
                                    business
                                } else {
                                    business.copy(name = "${business.name} in $location", address = location)
                                }
                            },
                        )
                    }
                }
        }
}
