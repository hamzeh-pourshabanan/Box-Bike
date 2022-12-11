package com.example.location.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.location.DestinationsUiModel
import com.example.location.databinding.ItemViewDestinationBinding

class DestinationViewHolder(val binding: ItemViewDestinationBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(details: DestinationsUiModel) {
        with(binding){
            originImage.background = ResourcesCompat.getDrawable(binding.root.resources, details.image, null)
            originAddress.text = details.address
        }
    }

    companion object {
        fun create(parent: ViewGroup): DestinationViewHolder {
            return DestinationViewHolder(
                ItemViewDestinationBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false)
            )
        }
    }
}