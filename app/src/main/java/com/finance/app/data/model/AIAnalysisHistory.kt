package com.finance.app.data.model

import com.google.gson.annotations.SerializedName

data class AIAnalysisHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("ai_model_id")
    val modelId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("result")
    val result: String?,
    @SerializedName("created_at")
    val createdAt: String?
)
