package com.kaushalya.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaushalya.app.data.model.WorkImage
import com.kaushalya.app.databinding.ItemWorkImageBinding

class WorkImageAdapter : RecyclerView.Adapter<WorkImageAdapter.ViewHolder>() {

    private var images = listOf<WorkImage>()

    fun submitList(newList: List<WorkImage>) {
        images = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(images[position].imageUrl)
            .into(holder.binding.ivWork)
    }

    override fun getItemCount() = images.size

    class ViewHolder(val binding: ItemWorkImageBinding) : RecyclerView.ViewHolder(binding.root)
}
