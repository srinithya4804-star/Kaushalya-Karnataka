package com.kaushalya.app.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaushalya.app.data.model.*
import com.kaushalya.app.data.repository.WorkerRepository
import kotlinx.coroutines.launch

class WorkerViewModel : ViewModel() {
    private val repository = WorkerRepository()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _workers = MutableLiveData<List<Worker>>()
    val workers: LiveData<List<Worker>> = _workers

    private val _currentWorker = MutableLiveData<Worker?>()
    val currentWorker: LiveData<Worker?> = _currentWorker

    private val _services = MutableLiveData<List<Service>>()
    val services: LiveData<List<Service>> = _services

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> = _reviews

    private val _workImages = MutableLiveData<List<WorkImage>>()
    val workImages: LiveData<List<WorkImage>> = _workImages

    fun registerWorker(worker: Worker, imageUri: Uri?, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.registerWorker(worker, imageUri).fold(
                onSuccess = { 
                    _loading.value = false
                    onSuccess() 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    private var allWorkers = listOf<Worker>()

    fun loadWorkers(trade: String? = null) {
        _loading.value = true
        viewModelScope.launch {
            repository.getWorkers(trade).fold(
                onSuccess = { 
                    _loading.value = false
                    allWorkers = it
                    _workers.value = it 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    fun searchWorkers(query: String) {
        if (query.isEmpty()) {
            _workers.value = allWorkers
        } else {
            _workers.value = allWorkers.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.trade.contains(query, ignoreCase = true) ||
                it.location.contains(query, ignoreCase = true)
            }
        }
    }

    fun loadWorkerDetails(workerId: String) {
        _loading.value = true
        viewModelScope.launch {
            repository.getWorkerDetails(workerId).fold(
                onSuccess = { 
                    _loading.value = false
                    _currentWorker.value = it 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    fun addService(workerId: String, service: Service, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.addService(workerId, service).fold(
                onSuccess = { 
                    _loading.value = false
                    onSuccess() 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    fun loadServices(workerId: String) {
        viewModelScope.launch {
            repository.getServices(workerId).onSuccess { _services.value = it }
        }
    }

    fun addReview(workerId: String, review: Review, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.addReview(workerId, review).fold(
                onSuccess = { 
                    _loading.value = false
                    onSuccess() 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    fun loadReviews(workerId: String) {
        viewModelScope.launch {
            repository.getReviews(workerId).onSuccess { _reviews.value = it }
        }
    }

    fun uploadWorkImages(workerId: String, uris: List<Uri>, onSuccess: () -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            repository.uploadWorkImages(workerId, uris).fold(
                onSuccess = { 
                    _loading.value = false
                    onSuccess() 
                },
                onFailure = { 
                    _loading.value = false
                    _error.value = it.message 
                }
            )
        }
    }

    fun loadWorkImages(workerId: String) {
        viewModelScope.launch {
            repository.getWorkImages(workerId).onSuccess { _workImages.value = it }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
