package com.finance.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateIncomeRequest(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("type")
    val type: String,
    @SerializedName("income_time")
    val incomeTime: String
)

data class UpdateIncomeRequest(
    @SerializedName("amount")
    val amount: Double? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("income_time")
    val incomeTime: String? = null
)
