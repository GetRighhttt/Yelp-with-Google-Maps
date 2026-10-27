package com.example.yelpclone.presentation.business.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.yelpclone.BuildConfig
import com.example.yelpclone.R
import com.example.yelpclone.databinding.ActivityLocationMapBinding
import com.example.yelpclone.presentation.business.model.BusinessUiModel
import com.example.yelpclone.presentation.common.applyWindowInsets
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Displays a validated location and returns through the normal Android back stack.
 *
 * @author Stefan Bayne
 * @since 2023-04-28
 */
class BusinessMapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item =
            IntentCompat.getParcelableExtra(
                intent,
                BusinessDetailsActivity.EXTRA_ITEM,
                BusinessUiModel::class.java,
            )
        val latitude = item?.latitude
        val longitude = item?.longitude
        if (!BuildConfig.MAPS_CONFIGURED || item == null || latitude == null || longitude == null ||
            !latitude.isFinite() ||
            !longitude.isFinite() ||
            latitude !in -90.0..90.0 ||
            longitude !in -180.0..180.0
        ) {
            finish()
            return
        }
        val binding = ActivityLocationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets(binding.root)
        binding.toolbar.title = item.name
        binding.address.text = item.address
        binding.toolbar.setNavigationOnClickListener { finish() }
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync { map ->
            val position = LatLng(latitude, longitude)
            map.addMarker(MarkerOptions().position(position).title(item.name))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            map.uiSettings.isZoomControlsEnabled = true
        }
    }
}
