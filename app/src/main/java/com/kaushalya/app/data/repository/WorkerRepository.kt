package com.kaushalya.app.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.kaushalya.app.data.model.*
import kotlinx.coroutines.tasks.await
import java.util.*

class WorkerRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val workersCollection = firestore.collection("workers")

    suspend fun registerWorker(worker: Worker, imageUri: Uri?): Result<String> {
        return try {
            var imageUrl = ""
            if (imageUri != null) {
                val ref = storage.reference.child("profiles/${UUID.randomUUID()}")
                ref.putFile(imageUri).await()
                imageUrl = ref.downloadUrl.await().toString()
            }
            val finalWorker = worker.copy(profileImageUrl = imageUrl)
            workersCollection.document(worker.id).set(finalWorker).await()
            Result.success(worker.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkers(trade: String? = null): Result<List<Worker>> {
        return try {
            val query = if (trade != null && trade != "All") {
                workersCollection.whereEqualTo("trade", trade)
            } else {
                workersCollection
            }
            val snapshot = query.get().await()
            val workers = snapshot.toObjects(Worker::class.java)
            Result.success(workers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkerDetails(workerId: String): Result<Worker> {
        return try {
            val snapshot = workersCollection.document(workerId).get().await()
            val worker = snapshot.toObject(Worker::class.java)
            if (worker != null) Result.success(worker)
            else Result.failure(Exception("Worker not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addService(workerId: String, service: Service): Result<Unit> {
        return try {
            workersCollection.document(workerId).collection("services").add(service).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServices(workerId: String): Result<List<Service>> {
        return try {
            val snapshot = workersCollection.document(workerId).collection("services").get().await()
            val services = snapshot.toObjects(Service::class.java)
            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addReview(workerId: String, review: Review): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val workerRef = workersCollection.document(workerId)
                val workerDoc = transaction.get(workerRef)
                val currentAvg = workerDoc.getDouble("avgRating") ?: 0.0
                val currentTotal = workerDoc.getLong("totalReviews") ?: 0L
                
                val newTotal = currentTotal + 1
                val newAvg = ((currentAvg * currentTotal) + review.rating) / newTotal
                
                val reviewRef = workerRef.collection("reviews").document()
                transaction.set(reviewRef, review)
                transaction.update(workerRef, "avgRating", newAvg, "totalReviews", newTotal)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviews(workerId: String): Result<List<Review>> {
        return try {
            val snapshot = workersCollection.document(workerId).collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            val reviews = snapshot.toObjects(Review::class.java)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadWorkImages(workerId: String, uris: List<Uri>): Result<Unit> {
        return try {
            uris.forEach { uri ->
                val ref = storage.reference.child("work_images/$workerId/${UUID.randomUUID()}")
                ref.putFile(uri).await()
                val url = ref.downloadUrl.await().toString()
                workersCollection.document(workerId).collection("workImages")
                    .add(WorkImage(imageUrl = url)).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorkImages(workerId: String): Result<List<WorkImage>> {
        return try {
            val snapshot = workersCollection.document(workerId).collection("workImages")
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            val images = snapshot.toObjects(WorkImage::class.java)
            Result.success(images)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
