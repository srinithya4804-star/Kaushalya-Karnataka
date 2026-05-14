package com.kaushalya.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaushalya.app.R
import com.kaushalya.app.data.model.Worker
import com.kaushalya.app.databinding.ItemWorkerBinding

class WorkerAdapter(
    private val onWorkerClick: (Worker) -> Unit
) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>() {

    private var workers = listOf<Worker>()

    fun submitList(newList: List<Worker>) {
        workers = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val binding = ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.bind(workers[position])
    }

    override fun getItemCount() = workers.size

    inner class WorkerViewHolder(private val binding: ItemWorkerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(worker: Worker) {
            binding.tvName.text = worker.name
            binding.tvTrade.text = worker.trade
            binding.tvLocation.text = worker.location
            binding.tvRating.text = String.format("%.1f", worker.avgRating)
            binding.tvReviews.text = "(${worker.totalReviews} reviews)"
            
            Glide.with(binding.ivProfile)
                .load(worker.profileImageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(binding.ivProfile)

            binding.root.setOnClickListener { onWorkerClick(worker) }
        }
    }
}
