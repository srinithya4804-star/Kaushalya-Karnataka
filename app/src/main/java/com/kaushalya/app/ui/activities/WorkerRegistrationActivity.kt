package com.kaushalya.app.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kaushalya.app.data.model.Worker
import com.kaushalya.app.databinding.ActivityWorkerRegistrationBinding
import com.kaushalya.app.viewmodel.WorkerViewModel
import java.util.*

class WorkerRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkerRegistrationBinding
    private val viewModel: WorkerViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivProfile.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTradeDropdown()

        binding.ivProfile.setOnClickListener { pickImage.launch("image/*") }

        binding.btnRegister.setOnClickListener { registerWorker() }

        observeViewModel()
    }

    private fun setupTradeDropdown() {
        val trades = arrayOf("Electrician", "Plumber", "Carpenter", "Painter", "Gardener", "Cleaner")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, trades)
        binding.actTrade.setAdapter(adapter)
    }

    private fun registerWorker() {
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val trade = binding.actTrade.text.toString()
        val location = binding.etLocation.text.toString()

        if (name.isEmpty() || phone.isEmpty() || trade.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val workerId = UUID.randomUUID().toString() // Mock ID for demonstration
        val worker = Worker(id = workerId, name = name, phone = phone, trade = trade, location = location)

        viewModel.registerWorker(worker, selectedImageUri) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
            val intent = android.content.Intent(this, WorkerDashboardActivity::class.java).apply {
                putExtra("WORKER_ID", workerId)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
}
