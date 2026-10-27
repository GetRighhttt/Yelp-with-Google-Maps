package com.example.yelpclone.presentation.business.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yelpclone.R
import com.example.yelpclone.databinding.YelpListItemBinding
import com.example.yelpclone.presentation.business.model.BusinessUiModel

/**
 * Binds feature display values and compares list identity separately from content.
 *
 * @author Stefan Bayne
 * @since 2023-04-23
 */
class BusinessAdapter(
    private val onClick: (BusinessUiModel) -> Unit,
) : ListAdapter<BusinessUiModel, BusinessAdapter.ViewHolder>(Diff) {
    class ViewHolder(
        private val binding: YelpListItemBinding,
        private val onClick: (BusinessUiModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BusinessUiModel) {
            binding.title.text = item.name
            binding.category.text = item.category
            binding.rating.text =
                binding.root.context.getString(
                    R.string.rating_summary,
                    item.rating,
                    binding.root.resources.getQuantityString(
                        R.plurals.review_count,
                        item.reviewCount,
                        item.reviewCount,
                    ),
                )
            binding.distance.text = item.distance
            binding.address.text = item.address
            Glide
                .with(binding.photo)
                .load(item.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_business)
                .error(R.drawable.placeholder_business)
                .into(binding.photo)
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder = ViewHolder(YelpListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClick)

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) = holder.bind(getItem(position))

    private object Diff : DiffUtil.ItemCallback<BusinessUiModel>() {
        override fun areItemsTheSame(
            oldItem: BusinessUiModel,
            newItem: BusinessUiModel,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: BusinessUiModel,
            newItem: BusinessUiModel,
        ) = oldItem == newItem
    }
}
