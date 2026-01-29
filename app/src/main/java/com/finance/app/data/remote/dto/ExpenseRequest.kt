package com.finance.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateExpenseRequest(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("expense_time")
    val expenseTime: String
)

data class UpdateExpenseRequest(
    @SerializedName("amount")
    val amount: Double? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("expense_time")
    val expenseTime: String? = null
)
