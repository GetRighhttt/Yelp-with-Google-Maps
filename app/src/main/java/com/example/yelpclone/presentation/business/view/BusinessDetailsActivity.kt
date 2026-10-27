package com.example.yelpclone.presentation.business.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.example.yelpclone.BuildConfig
import com.example.yelpclone.R
import com.example.yelpclone.databinding.ActivityYelpDetailsBinding
import com.example.yelpclone.presentation.business.model.BusinessUiModel
import com.example.yelpclone.presentation.common.applyWindowInsets

/**
 * Renders the selected display model and opens its map without replacing the list activity.
 *
 * @author Stefan Bayne
 * @since 2023-04-28
 */
class BusinessDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = IntentCompat.getParcelableExtra(intent, EXTRA_ITEM, BusinessUiModel::class.java)
        if (item == null) {
            finish()
            return
        }
        val binding = ActivityYelpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.title.text = item.name
        binding.subtitle.text = item.category
        binding.address.text = item.address.ifBlank { getString(R.string.unavailable) }
        binding.phone.text = item.phone.ifBlank { getString(R.string.unavailable) }
        binding.rating.text =
            getString(
                R.string.rating_summary,
                item.rating,
                binding.root.resources.getQuantityString(R.plurals.review_count, item.reviewCount, item.reviewCount),
            )
        binding.distance.text = item.distance
        Glide
            .with(
                this,
            ).load(
                item.imageUrl,
            ).centerCrop()
            .placeholder(
                R.drawable.placeholder_business,
            ).error(R.drawable.placeholder_business)
            .into(binding.photo)
        binding.mapsButton.isEnabled = BuildConfig.MAPS_CONFIGURED && item.latitude != null && item.longitude != null
        if (!binding.mapsButton.isEnabled) {
            binding.mapsButton.setText(
                if (BuildConfig.MAPS_CONFIGURED) R.string.no_map_location else R.string.map_unavailable,
            )
        }
        binding.mapsButton.setOnClickListener {
            startActivity(Intent(this, BusinessMapActivity::class.java).putExtra(EXTRA_ITEM, item))
        }
    }

    companion object {
        const val EXTRA_ITEM = "com.example.yelpclone.business.ITEM"
    }
}
