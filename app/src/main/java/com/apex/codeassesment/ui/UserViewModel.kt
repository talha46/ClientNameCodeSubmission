package com.apex.codeassesment.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.codeassesment.data.Resource
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.model.UsersResponse
import com.codebase.waqfe.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel(),
    CoroutineScope {
    val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _userFlow

    val _usersFlow = MutableStateFlow<Resource<UsersResponse>?>(null)
    val usersFlow: StateFlow<Resource<UsersResponse>?> = _usersFlow

    val showLoading = MutableLiveData<Boolean>()
    val getUserLiveData = SingleLiveEvent<User?>()
    val getUsersLiveData = SingleLiveEvent<List<User>?>()
    val showError = SingleLiveEvent<String>()

    // Coroutine's background job
    private val job = Job()

    // Define default thread for Coroutine as Main and add job
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        // Clear our job when the linked activity is destroyed to avoid memory leaks
        job.cancel()
    }

    fun getSavedUser() = userRepository.getSavedUser()

    //Using State Flows for Jetpack Compose Activity
    fun getUserFromRemote(forceUpdate: Boolean) = viewModelScope.launch {
        _userFlow.value = userRepository.getUser(forceUpdate = forceUpdate)
    }

    fun getUsersFromRemote(count: Int) = viewModelScope.launch {
        _usersFlow.value = Resource.Loading
        val result = userRepository.getUsersFromRemote(count = count)
        _usersFlow.value = result
    }

    //Using Live Data for Normal Activity
    fun getUser(forceUpdate: Boolean) {
        launch {
            showLoading.value = true
            getUserLiveData.value = withContext(Dispatchers.IO) {
                userRepository.getUser(forceUpdate)
            }
            showLoading.value = false
        }
    }

    fun getUsers(count: Int) {
        launch {
            val result = withContext(Dispatchers.IO) {
                userRepository.getUsersFromRemote(count = count)
            }
            when (result) {
                is Resource.Success -> {
                    showLoading.value = false
                    getUsersLiveData.value = result.response?.results
                }

                is Resource.Failure -> {
                    showLoading.value = false
                    showError.value = result.error?.toString()
                }

                else -> {
                    showLoading.value = true
                }
            }
        }
    }
}