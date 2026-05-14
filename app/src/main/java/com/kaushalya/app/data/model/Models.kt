package com.kaushalya.app.data.model

import com.google.firebase.firestore.DocumentId

data class Worker(
    @DocumentId val id: String = "",
    val name: String = "",
    val phone: String = "",
    val trade: String = "",
    val location: String = "",
    val profileImageUrl: String = "",
    val avgRating: Double = 0.0,
    val totalReviews: Int = 0
)

data class Service(
    @DocumentId val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val priceType: String = "" // "Fixed" or "Starting At"
)

data class Review(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class WorkImage(
    @DocumentId val id: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
