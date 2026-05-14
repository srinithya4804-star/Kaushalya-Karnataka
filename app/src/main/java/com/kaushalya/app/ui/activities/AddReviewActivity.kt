package com.kaushalya.app.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kaushalya.app.data.model.Review
import com.kaushalya.app.databinding.ActivityAddReviewBinding
import com.kaushalya.app.viewmodel.WorkerViewModel

class AddReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddReviewBinding
    private val viewModel: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val workerId = intent.getStringExtra("WORKER_ID") ?: return

        binding.btnSubmit.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.etComment.text.toString()

            if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show()
                return
            }

            val review = Review(
                userId = "test_user_id", // Should come from Auth
                userName = "Local Customer",
                rating = rating,
                comment = comment
            )

            viewModel.addReview(workerId, review) {
                Toast.makeText(this, "Review Submitted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
