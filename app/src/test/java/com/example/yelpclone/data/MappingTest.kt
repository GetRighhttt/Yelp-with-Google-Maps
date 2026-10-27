package com.example.yelpclone.data

import com.example.yelpclone.data.business.mapper.toDomain
import com.example.yelpclone.data.business.model.BusinessDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifies business JSON mapping, optional fields, and coordinate validation.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
class MappingTest {
    private val gson = Gson()

    @Test fun `business without optional fields remains usable`() {
        val business =
            gson
                .fromJson(
                    """{"id":"cafe","name":"Cafe","categories":[]}""",
                    BusinessDto::class.java,
                ).toDomain()
        assertEquals("", business.category)
        assertEquals("", business.address)
        assertNull(business.latitude)
        assertNull(business.longitude)
    }

    @Test fun `invalid coordinates and distances do not become map positions`() {
        val business =
            gson
                .fromJson(
                    """{"id":"cafe","name":"Cafe","coordinates":{"latitude":100,"longitude":-190},"distance":-4}""",
                    BusinessDto::class.java,
                ).toDomain()
        assertNull(business.latitude)
        assertNull(business.longitude)
        assertNull(business.distanceMeters)
    }
}
