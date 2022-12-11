package com.example.location.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.location.DestinationsUiModel

class DestinationAdapter: ListAdapter<DestinationsUiModel, DestinationViewHolder>(
    REFUND_DETAILS_COMPARATOR
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinationViewHolder = DestinationViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: DestinationViewHolder,
        position: Int
    ) = holder.bind(details = getItem(position))

    companion object {
        private val REFUND_DETAILS_COMPARATOR =
            object : DiffUtil.ItemCallback<DestinationsUiModel>() {
                override fun areItemsTheSame(
                    oldItem: DestinationsUiModel,
                    newItem: DestinationsUiModel
                ): Boolean =
                    oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: DestinationsUiModel,
                    newItem: DestinationsUiModel
                ): Boolean =
                    oldItem.address == newItem.address &&
                            oldItem.image == oldItem.image
            }
    }
}