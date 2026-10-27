package com.example.yelpclone.presentation.business.view

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.yelpclone.R
import com.example.yelpclone.databinding.ActivityMainBinding
import com.example.yelpclone.databinding.DialogLocationBinding
import com.example.yelpclone.presentation.business.BusinessViewModel
import com.example.yelpclone.presentation.business.model.BusinessScreenState
import com.example.yelpclone.presentation.common.applyWindowInsets
import com.example.yelpclone.presentation.common.discoveryColumns
import com.example.yelpclone.presentation.common.messageResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Renders lifecycle-aware screen state and forwards user actions to the feature ViewModel.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
@AndroidEntryPoint
class BusinessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: BusinessViewModel by viewModels()
    private var locationDialog: AlertDialog? = null
    private var locationBinding: DialogLocationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setOnExitAnimationListener { provider ->
            provider.view
                .animate()
                .alpha(0f)
                .setDuration(240L)
                .withEndAction { provider.remove() }
                .start()
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets(binding.root)
        val adapter =
            BusinessAdapter { item ->
                startActivity(
                    Intent(
                        this,
                        BusinessDetailsActivity::class.java,
                    ).putExtra(BusinessDetailsActivity.EXTRA_ITEM, item),
                )
            }
        binding.results.layoutManager = GridLayoutManager(this, resources.discoveryColumns())
        binding.results.adapter = adapter
        binding.search.setQuery(viewModel.uiState.value.query, false)
        binding.search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.search(query.orEmpty())
                    binding.search.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?) = false
            },
        )
        mapOf(
            binding.coffee to "coffee",
            binding.restaurants to "restaurants",
            binding.brunch to "brunch",
            binding.pizza to "pizza",
        ).forEach { (chip, term) ->
            chip.setOnClickListener {
                binding.search.setQuery(term, false)
                binding.search.clearFocus()
                viewModel.search(term)
            }
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.change_location) {
                viewModel.openLocationEditor()
                true
            } else {
                false
            }
        }
        binding.retry.setOnClickListener { viewModel.retry() }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.toolbar.title = state.location
                    binding.discoveryHeading.text = getString(R.string.discover, state.location)
                    binding.sourceAttribution.text = getString(R.string.source_yelp, state.location)
                    renderLocationEditor(state)
                    mapOf(
                        binding.coffee to "coffee",
                        binding.restaurants to "restaurants",
                        binding.brunch to "brunch",
                        binding.pizza to "pizza",
                    ).forEach { (button, term) -> button.isChecked = state.query.equals(term, ignoreCase = true) }
                    binding.progress.isVisible = state.isLoading
                    binding.statePanel.isVisible = !state.isLoading && (state.error != null || state.items.isEmpty())
                    binding.status.text =
                        state.error?.let { error -> getString(error.messageResource()) }
                            ?: getString(R.string.empty_results)
                    binding.retry.isVisible = !state.isLoading && state.error != null
                    adapter.submitList(state.items)
                    binding.resultCount.text =
                        resources.getQuantityString(R.plurals.business_count, state.items.size, state.items.size)
                }
            }
        }
    }

    private fun renderLocationEditor(state: BusinessScreenState) {
        if (!state.isEditingLocation) {
            locationDialog?.dismiss()
            locationDialog = null
            locationBinding = null
            return
        }
        if (locationDialog == null) {
            val editor = DialogLocationBinding.inflate(layoutInflater)
            locationBinding = editor
            editor.locationField.counterMaxLength = viewModel.locationCharacterLimit
            editor.locationInput.setText(state.locationDraft)
            editor.locationInput.doAfterTextChanged { viewModel.updateLocationDraft(it.toString()) }
            editor.locationInput.setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.confirmLocation()
                    true
                } else {
                    false
                }
            }
            locationDialog =
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.location_dialog_title)
                    .setView(editor.root)
                    .setPositiveButton(R.string.search_here, null)
                    .setNegativeButton(R.string.cancel) { _, _ -> viewModel.cancelLocationEdit() }
                    .setOnCancelListener { viewModel.cancelLocationEdit() }
                    .create()
                    .also { dialog ->
                        dialog.setOnShowListener {
                            dialog
                                .getButton(
                                    AlertDialog.BUTTON_POSITIVE,
                                ).setOnClickListener { viewModel.confirmLocation() }
                            editor.locationInput.requestFocus()
                            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        }
                        dialog.show()
                    }
        }
        locationBinding?.locationField?.error = state.locationError?.let { getString(it.messageResource()) }
    }

    override fun onDestroy() {
        locationDialog?.dismiss()
        locationDialog = null
        locationBinding = null
        super.onDestroy()
    }
}
