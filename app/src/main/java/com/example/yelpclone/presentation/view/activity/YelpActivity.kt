package com.example.yelpclone.presentation.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yelpclone.R
import com.example.yelpclone.core.events.SearchEvent
import com.example.yelpclone.databinding.ActivityMainBinding
import com.example.yelpclone.presentation.view.adapter.YelpBusinessAdapter
import com.example.yelpclone.presentation.view.details.YelpDetailsActivity
import com.example.yelpclone.presentation.view.splashscreens.SecondStartActivity
import com.example.yelpclone.presentation.view.viewmodels.YelpViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class YelpActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private val viewModel: YelpViewModel by viewModels()
    private lateinit var yelpAdapter: YelpBusinessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        determineSearchState()
        onMenuItemSelection()
        setupSearchView()
        onBackPressedInitializer()
    }

    private fun initRecyclerView() {
        binding.rvYelpList.apply {
            hasFixedSize()
            yelpAdapter =
                YelpBusinessAdapter(this@YelpActivity)
            adapter = yelpAdapter
            layoutManager = LinearLayoutManager(this@YelpActivity)
        }.also {
            it.smoothScrollToPosition(0)
        }
    }

    private fun determineSearchState() {
        binding.apply {
            lifecycleScope.launch {
                /*
                Flow isn't lifecycle aware so we must flow with lifecycle to handle resource
                consumption.
                */
                viewModel.searchState.flowWithLifecycle(lifecycle).collect { response ->
                    when (response) {
                        is SearchEvent.Failure -> {
                            createSnackBar("Error when fetching Data!")
                            pbMain.visibility = View.GONE
                            Log.d(MAIN, "Failed to update UI with data: ${response.errorMessage}")
                        }

                        is SearchEvent.Loading -> {
                            createSnackBar("Loading...")
                            pbMain.visibility = View.VISIBLE
                            Log.d(MAIN, "Loading main...")
                        }

                        is SearchEvent.Success -> {
                            if (response.results!!.restaurants.isEmpty()) {
                                createSnackBar("Results are Empty!")
                                pbMain.visibility = View.GONE
                                noResults.visibility = View.VISIBLE
                                Log.d(
                                    MAIN,
                                    "Failed to update UI with data: ${response.errorMessage}"
                                )
                            } else {
                                response.results.let {
                                    yelpAdapter.differ.submitList(it.restaurants.toList())
                                    yelpAdapter.setOnItemClickListener {
                                        // details
                                        val detailIntent =
                                            Intent(
                                                this@YelpActivity,
                                                YelpDetailsActivity::class.java
                                            )
                                        val bundle = Bundle().apply {
                                            detailIntent.putExtra(EXTRA_ITEM_ID_MAIN, it)
                                        }
                                        startActivity(detailIntent)
                                        overridePendingTransition(
                                            R.anim.slide_in_left_animation,
                                            R.anim.slide_out_right
                                        )
                                        finish()
                                    }
                                }
                                createSnackBar("Successfully fetched Data!")
                                pbMain.visibility = View.GONE
                                Log.d(
                                    MAIN,
                                    "Successfully updated UI with data: ${response.results}"
                                )
                            }
                        }

                        is SearchEvent.Idle -> {
                            Log.d(MAIN, "Idle State currently...")
                        }
                    }
                }
            }
        }
    }

    private fun setupSearchView() = binding.searchView.apply {
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.pbMain.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        binding.rvYelpList.smoothScrollToPosition(0)
                        viewModel.getBusinesses(query)
                        clearFocus()
                        delay(1500)
                        binding.pbMain.visibility = View.GONE
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun onMenuItemSelection() {
        binding.apply {
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.user -> {
                        materialDialogBuilder(
                            this@YelpActivity,
                            "Navigation".uppercase(),
                            "To see a list of Yelp users, click OK. " +
                                    "Otherwise, click cancel to exit."
                        )
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun onBackPressedInitializer() = onBackPressedDispatcher.addCallback(
        this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backIntent = Intent(this@YelpActivity, UserActivity::class.java)
                startActivity(backIntent)
            }
        }
    )

    private fun createSnackBar(message: String) = Snackbar.make(
        binding.root, message, Snackbar.LENGTH_SHORT
    ).show()

    private fun materialDialogBuilder(
        context: Context,
        title: String,
        message: String
    ) = object : MaterialAlertDialogBuilder(this) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(this@YelpActivity, SecondStartActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val MAIN = "MAIN_ACTIVITY"
        const val EXTRA_ITEM_ID_MAIN = "EXTRA_ITEM_ID"
    }
}
