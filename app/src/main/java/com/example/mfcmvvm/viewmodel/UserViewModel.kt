package com.example.mfcmvvm.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mfcmvvm.model.User
import com.example.mfcmvvm.repository.UserRepository
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val users: List<User>) : UiState()
    data class Error(val message: String) : UiState()
    object Offline : UiState()
}

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _allUsers = MutableLiveData<List<User>>()

    private val _filteredUsers = MutableLiveData<List<User>>()
    val filteredUsers: LiveData<List<User>> = _filteredUsers

    private var currentQuery = ""

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getUsers()
            result.onSuccess { users ->
                _allUsers.value = users
                applyFilter(users)
                _uiState.value = UiState.Success(users)
            }.onFailure { e ->
                _uiState.value = UiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun searchUsers(query: String) {
        currentQuery = query
        val all = _allUsers.value ?: return
        applyFilter(all)
    }

    private fun applyFilter(users: List<User>) {
        if (currentQuery.isBlank()) {
            _filteredUsers.value = users
        } else {
            val q = currentQuery.lowercase()
            _filteredUsers.value = users.filter {
                it.name.lowercase().contains(q) || it.email.lowercase().contains(q)
            }
        }
    }
}

class UserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(UserRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
