package com.kaushalya.app.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.kaushalya.app.R
import com.kaushalya.app.databinding.ActivityWorkerDashboardBinding
import com.kaushalya.app.ui.adapters.ServiceAdapter
import com.kaushalya.app.ui.adapters.WorkImageAdapter
import com.kaushalya.app.viewmodel.WorkerViewModel

class WorkerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerDashboardBinding
    private val viewModel: WorkerViewModel by viewModels()
    private var workerId: String? = null
    
    private val serviceAdapter = ServiceAdapter()
    private val workImageAdapter = WorkImageAdapter()

    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty() && workerId != null) {
            viewModel.uploadWorkImages(workerId!!, uris) {
                viewModel.loadWorkImages(workerId!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workerId = intent.getStringExtra("WORKER_ID")
        
        setupRecyclerViews()
        observeViewModel()

        workerId?.let {
            viewModel.loadWorkerDetails(it)
            viewModel.loadServices(it)
            viewModel.loadWorkImages(it)
        }

        binding.btnAddService.setOnClickListener {
            val intent = Intent(this, AddServiceActivity::class.java)
            intent.putExtra("WORKER_ID", workerId)
            startActivity(intent)
        }

        binding.btnUploadWork.setOnClickListener { pickImages.launch("image/*") }
    }

    private fun setupRecyclerViews() {
        binding.rvServices.layoutManager = LinearLayoutManager(this)
        binding.rvServices.adapter = serviceAdapter

        binding.rvWorkImages.layoutManager = GridLayoutManager(this, 3)
        binding.rvWorkImages.adapter = workImageAdapter
    }

    private fun observeViewModel() {
        viewModel.currentWorker.observe(this) { worker ->
            worker?.let {
                binding.tvName.text = it.name
                binding.tvRating.text = "Rating: ${String.format("%.1f", it.avgRating)} (${it.totalReviews} reviews)"
                Glide.with(this).load(it.profileImageUrl).placeholder(R.drawable.ic_profile_placeholder).into(binding.ivProfile)
            }
        }

        viewModel.services.observe(this) { serviceAdapter.submitList(it) }
        viewModel.workImages.observe(this) { workImageAdapter.submitList(it) }
    }

    override fun onResume() {
        super.onResume()
        workerId?.let { viewModel.loadServices(it) }
    }
}
