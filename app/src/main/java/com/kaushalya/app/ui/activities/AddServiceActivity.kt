package com.kaushalya.app.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kaushalya.app.data.model.Service
import com.kaushalya.app.databinding.ActivityAddServiceBinding
import com.kaushalya.app.viewmodel.WorkerViewModel

class AddServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddServiceBinding
    private val viewModel: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workerId = intent.getStringExtra("WORKER_ID") ?: return

        binding.btnSave.setOnClickListener {
            val name = binding.etServiceName.text.toString()
            val priceStr = binding.etPrice.text.toString()
            val priceType = if (binding.rbFixed.isChecked) "Fixed" else "Starting At"

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            val service = Service(name = name, price = priceStr.toDouble(), priceType = priceType)
            viewModel.addService(workerId, service) {
                Toast.makeText(this, "Service Added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
