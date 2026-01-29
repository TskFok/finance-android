package com.finance.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnalysisRequest(
    @SerializedName("model_id")
    val modelId: Int,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    @SerializedName("user_id")
    val userId: Int? = null
)

data class AIChatRequest(
    @SerializedName("model_id")
    val modelId: Int,
    @SerializedName("message")
    val message: String
)

data class SSEEvent(
    val type: String, // "delta", "done", "error"
    val content: String?
)
