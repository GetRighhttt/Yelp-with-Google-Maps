package com.example.yelpclone

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.yelpclone.presentation.business.view.BusinessDetailsActivity
import com.example.yelpclone.presentation.business.view.BusinessMapActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies malformed internal navigation closes safely instead of crashing on missing extras.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @Test fun missingBusinessPayloadClosesDetails() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch<BusinessDetailsActivity>(Intent(context, BusinessDetailsActivity::class.java)).use {
            assertEquals(Lifecycle.State.DESTROYED, it.state)
        }
    }

    @Test fun missingLocationClosesMap() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ActivityScenario.launch<BusinessMapActivity>(Intent(context, BusinessMapActivity::class.java)).use {
            assertEquals(Lifecycle.State.DESTROYED, it.state)
        }
    }
}
