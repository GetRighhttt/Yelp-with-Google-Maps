package com.example.yelpclone.presentation.common

import android.content.res.Resources

/**
 * Chooses readable grid columns from available width and the user's text size.
 *
 * @author Stefan Bayne
 * @since 2026-10-26
 */
fun Resources.discoveryColumns(): Int =
    if (configuration.fontScale >= 1.3f) 1 else (configuration.screenWidthDp / 180).coerceIn(1, 3)
