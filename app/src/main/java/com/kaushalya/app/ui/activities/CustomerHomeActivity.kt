package com.kaushalya.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaushalya.app.ui.adapters.WorkerAdapter
import com.google.android.material.chip.Chip
import com.kaushalya.app.databinding.ActivityCustomerHomeBinding
import com.kaushalya.app.viewmodel.WorkerViewModel

class CustomerHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerHomeBinding
    private val viewModel: WorkerViewModel by viewModels()
    private lateinit var adapter: WorkerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        viewModel.loadWorkers()

        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchWorkers(text.toString())
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = findViewById<Chip>(checkedId)
            val trade = chip?.text?.toString() ?: "All"
            viewModel.loadWorkers(trade)
        }
    }

    private fun setupRecyclerView() {
        adapter = WorkerAdapter { worker ->
            val intent = Intent(this, WorkerProfileActivity::class.java)
            intent.putExtra("WORKER_ID", worker.id)
            startActivity(intent)
        }
        binding.rvWorkers.layoutManager = LinearLayoutManager(this)
        binding.rvWorkers.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.workers.observe(this) { adapter.submitList(it) }
        viewModel.loading.observe(this) { binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE }
    }
}
