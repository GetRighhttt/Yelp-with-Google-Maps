package com.example.yelpclone.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Owns the application-wide Hilt dependency graph.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
@HiltAndroidApp
class SearchApplicationClass : Application()
