package com.finance.app.data.model

import com.google.gson.annotations.SerializedName

data class AIChatHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("ai_model_id")
    val modelId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_text")
    val userText: String?,
    @SerializedName("ai_text")
    val aiText: String?,
    @SerializedName("created_at")
    val createdAt: String?
)
