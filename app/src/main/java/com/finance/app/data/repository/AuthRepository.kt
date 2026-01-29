package com.finance.app.data.repository

import com.finance.app.data.local.PreferencesManager
import com.finance.app.data.model.User
import com.finance.app.data.remote.ApiService
import com.finance.app.data.remote.dto.LoginRequest
import com.finance.app.data.remote.dto.LoginResponse
import com.finance.app.data.remote.dto.RegisterRequest
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.code == 200 && body.data != null) {
                    val loginResponse = body.data
                    preferencesManager.saveToken(loginResponse.token)
                    preferencesManager.saveUserId(loginResponse.userInfo.id)
                    Resource.Success(loginResponse)
                } else {
                    Resource.Error(body?.message ?: "登录失败: ${response.code()}")
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "HTTP ${response.code()}"
                Resource.Error("登录失败: $errorBody")
            }
        } catch (e: java.net.UnknownHostException) {
            Resource.Error("无法连接到服务器，请检查网络设置和服务器地址")
        } catch (e: java.net.ConnectException) {
            Resource.Error("连接失败，请确保服务器正在运行")
        } catch (e: java.net.SocketTimeoutException) {
            Resource.Error("连接超时，请检查网络")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误: ${e.javaClass.simpleName}")
        }
    }
    
    suspend fun register(username: String, password: String, email: String?): Resource<User> {
        return try {
            val response = apiService.register(RegisterRequest(username, password, email))
            if (response.isSuccessful && response.body()?.code == 200) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "注册失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun getProfile(): Resource<User> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body()?.code == 200) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "获取用户信息失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun logout() {
        preferencesManager.clear()
    }
    
    fun isLoggedIn(): Flow<Boolean> = flow {
        emit(preferencesManager.getTokenSync() != null)
    }
}
