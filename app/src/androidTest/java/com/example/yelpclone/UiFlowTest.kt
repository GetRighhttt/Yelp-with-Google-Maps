package com.example.yelpclone

import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.yelpclone.presentation.business.view.BusinessActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Exercises list, search, error recovery, navigation, and recreation using isolated sample data.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
@HiltAndroidTest
class UiFlowTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Before fun inject() {
        hiltRule.inject()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @Test fun searchShortcutsAndBackKeepTheList() {
        launch().use {
            onView(withText("The Neighborhood Cafe")).check(matches(isDisplayed()))
            it.onActivity { activity ->
                assertTrue(
                    WindowCompat
                        .getInsetsController(
                            activity.window,
                            activity.window.decorView,
                        ).isAppearanceLightStatusBars,
                )
            }
            onView(withId(R.id.coffee)).perform(click())
            onView(withId(R.id.coffee)).check(matches(isChecked()))
            screenshot("places")
            onView(withText("The Neighborhood Cafe")).perform(click())
            onView(
                withId(R.id.maps_button),
            ).check(matches(if (BuildConfig.MAPS_CONFIGURED) isEnabled() else isNotEnabled()))
            screenshot("place-details")
            if (BuildConfig.MAPS_CONFIGURED) {
                onView(withId(R.id.maps_button)).perform(click())
                onView(withId(R.id.map)).check(matches(isDisplayed()))
                pressBack()
                onView(withId(R.id.maps_button)).check(matches(isDisplayed()))
            }
            pressBack()
            onView(withText("The Neighborhood Cafe")).check(matches(isDisplayed()))
            it.recreate()
            onView(withText("The Neighborhood Cafe")).check(matches(isDisplayed()))
        }
    }

    @Test fun failuresAndEmptyResultsRecoverAfterAnotherSearch() {
        launch().use {
            search("offline")
            onView(withId(R.id.status)).check(matches(withText(R.string.error_network)))
            onView(withId(R.id.retry)).check(matches(isDisplayed()))
            search("nothing")
            onView(withId(R.id.status)).check(matches(withText(R.string.empty_results)))
            search("coffee")
            onView(withText("The Neighborhood Cafe")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun darkThemeRendersTheSameResults() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
        try {
            launch().use {
                onView(withText("The Neighborhood Cafe")).check(matches(isDisplayed()))
                it.onActivity { activity ->
                    assertFalse(
                        WindowCompat
                            .getInsetsController(
                                activity.window,
                                activity.window.decorView,
                            ).isAppearanceLightStatusBars,
                    )
                }
                screenshot("places-dark")
            }
        } finally {
            instrumentation.runOnMainSync {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    @Test fun changingLocationUpdatesResultsAndSurvivesRecreation() {
        launch().use { scenario ->
            onView(withId(R.id.coffee)).perform(click())
            onView(withId(R.id.change_location)).perform(click())
            onView(withId(R.id.location_input)).perform(replaceText(""))
            onView(withText(R.string.search_here)).perform(click())
            onView(withText(R.string.error_location)).check(matches(isDisplayed()))
            onView(withId(R.id.location_input)).perform(replaceText("Chicago, IL"))
            scenario.recreate()
            onView(withId(R.id.location_input)).check(matches(withText("Chicago, IL")))
            onView(withText(R.string.search_here)).perform(click())
            onView(isRoot()).perform(closeSoftKeyboard())
            onView(withText("The Neighborhood Cafe in Chicago, IL")).check(matches(isDisplayed()))
            onView(withId(R.id.coffee)).check(matches(isChecked()))
            screenshot("location-search")
            scenario.recreate()
            onView(withText("The Neighborhood Cafe in Chicago, IL")).check(matches(isDisplayed()))
            onView(withId(R.id.change_location)).perform(click())
            onView(withId(R.id.location_input)).check(matches(withText("Chicago, IL")))
            onView(withId(R.id.location_input)).perform(replaceText("60601"))
            onView(withText(R.string.cancel)).perform(click())
            onView(withText("The Neighborhood Cafe in Chicago, IL")).check(matches(isDisplayed()))
        }
    }

    private fun launch(): ActivityScenario<BusinessActivity> {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return ActivityScenario.launch(
            requireNotNull(context.packageManager.getLaunchIntentForPackage(context.packageName)),
        )
    }

    private fun search(query: String) {
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText(query), pressImeActionButton())
        onView(isRoot()).perform(closeSoftKeyboard())
    }

    private fun screenshot(name: String) {
        onView(isRoot()).perform(
            object : ViewAction {
                override fun getConstraints(): Matcher<View> = isRoot()

                override fun getDescription(): String =
                    "Finish the native launch transition before capturing the preview"

                override fun perform(
                    uiController: UiController,
                    view: View,
                ) {
                    uiController.loopMainThreadForAtLeast(500L)
                }
            },
        )
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val folder = File(instrumentation.targetContext.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(folder, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
