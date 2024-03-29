package com.example.yelpclone.presentation.view.splashscreens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.yelpclone.databinding.ActivitySecondStartBinding
import com.example.yelpclone.presentation.view.activity.UserActivity

class SecondStartActivity : AppCompatActivity() {

    private var _binding: ActivitySecondStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySecondStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.secondIvYelp.animate().apply {
            duration = 250L
            rotationYBy(360F)
        }.withEndAction {
            binding.secondIvYelp.animate().apply {
                rotationYBy(-360F)
            }
            // delays before moving to other activity
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    startActivity(
                        Intent(this@SecondStartActivity, UserActivity::class.java)
                    )
                    finish()
                }, 800
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}