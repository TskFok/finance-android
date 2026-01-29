package com.finance.app.data.remote.dto

import com.finance.app.data.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user_info")
    val userInfo: User
)
