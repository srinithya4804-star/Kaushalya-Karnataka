package com.kaushalya.app.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kaushalya.app.databinding.ActivityRoleSelectionBinding

class RoleSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoleSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWorker.setOnClickListener {
            startActivity(Intent(this, WorkerRegistrationActivity::class.java))
        }

        binding.btnCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerHomeActivity::class.java))
        }
    }
}
