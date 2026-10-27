package com.example.yelpclone.presentation.common

import android.content.res.Configuration
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Applies system and keyboard insets from the original padding without accumulating offsets.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
fun AppCompatActivity.applyWindowInsets(root: View) {
    enableEdgeToEdge()
    val left = root.paddingLeft
    val top = root.paddingTop
    val right = root.paddingRight
    val bottom = root.paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val bars =
            insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime(),
            )
        view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        val lightBars =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = lightBars
            isAppearanceLightNavigationBars = lightBars
        }
        insets.inset(bars.left, bars.top, bars.right, bars.bottom)
    }
    ViewCompat.requestApplyInsets(root)
}
