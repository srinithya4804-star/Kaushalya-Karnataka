package com.kaushalya.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kaushalya.app.R
import com.kaushalya.app.databinding.ActivityWorkerProfileBinding
import com.kaushalya.app.ui.adapters.ReviewAdapter
import com.kaushalya.app.ui.adapters.ServiceAdapter
import com.kaushalya.app.ui.adapters.WorkImageAdapter
import com.kaushalya.app.viewmodel.WorkerViewModel

class WorkerProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerProfileBinding
    private val viewModel: WorkerViewModel by viewModels()
    
    private val serviceAdapter = ServiceAdapter()
    private val reviewAdapter = ReviewAdapter()
    private val workImageAdapter = WorkImageAdapter()
    private var workerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workerId = intent.getStringExtra("WORKER_ID")
        
        setupRecyclerViews()
        observeViewModel()

        workerId?.let {
            viewModel.loadWorkerDetails(it)
            viewModel.loadServices(it)
            viewModel.loadReviews(it)
            viewModel.loadWorkImages(it)
        }

        binding.btnAddReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("WORKER_ID", workerId)
            startActivity(intent)
        }

        binding.btnHire.setOnClickListener {
            Toast.makeText(this, "Professional notified! They will contact you shortly.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerViews() {
        binding.rvServices.layoutManager = LinearLayoutManager(this)
        binding.rvServices.adapter = serviceAdapter

        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = reviewAdapter

        binding.rvWorkImages.layoutManager = GridLayoutManager(this, 3)
        binding.rvWorkImages.adapter = workImageAdapter
    }

    private fun observeViewModel() {
        viewModel.currentWorker.observe(this) { worker ->
            worker?.let {
                binding.tvName.text = it.name
                binding.tvTrade.text = it.trade
                binding.tvRating.text = "${String.format("%.1f", it.avgRating)} (${it.totalReviews} reviews)"
                Glide.with(this).load(it.profileImageUrl).placeholder(R.drawable.ic_profile_placeholder).into(binding.ivBackdrop)
            }
        }
        viewModel.services.observe(this) { serviceAdapter.submitList(it) }
        viewModel.reviews.observe(this) { reviewAdapter.submitList(it) }
        viewModel.workImages.observe(this) { workImageAdapter.submitList(it) }
    }
}
