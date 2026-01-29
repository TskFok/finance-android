package com.finance.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.app.data.repository.AuthRepository
import com.finance.app.di.AppContainer
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository: AuthRepository = AppContainer.getAuthRepository()
    
    private val _loginState = MutableStateFlow<Resource<com.finance.app.data.remote.dto.LoginResponse>?>(null)
    val loginState: StateFlow<Resource<com.finance.app.data.remote.dto.LoginResponse>?> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow<Resource<com.finance.app.data.model.User>?>(null)
    val registerState: StateFlow<Resource<com.finance.app.data.model.User>?> = _registerState.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _profileState = MutableStateFlow<Resource<com.finance.app.data.model.User>?>(null)
    val profileState: StateFlow<Resource<com.finance.app.data.model.User>?> = _profileState.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _loginState.value = Resource.Loading()
                val result = authRepository.login(username, password)
                _loginState.value = result
                if (result is Resource.Success) {
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                _loginState.value = Resource.Error(e.message ?: "登录失败")
            }
        }
    }
    
    fun register(username: String, password: String, email: String?) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading()
            _registerState.value = authRepository.register(username, password, email)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            _profileState.value = null
        }
    }
    
    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading()
            _profileState.value = authRepository.getProfile()
        }
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect {
                _isLoggedIn.value = it
            }
        }
    }
}
