package com.finance.app.data.model

import com.google.gson.annotations.SerializedName

data class ExpenseStatistics(
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("category_stats")
    val categoryStats: List<CategoryStat>? = null
)

data class CategoryStat(
    @SerializedName("category")
    val category: String,
    @SerializedName("total")
    val total: Double,
    @SerializedName("count")
    val count: Int,
    @SerializedName("percentage")
    val percentage: Double
)

data class IncomeExpenseSummary(
    @SerializedName("total_expense")
    val totalExpense: Double,
    @SerializedName("total_income")
    val totalIncome: Double
)

data class PageResponse<T>(
    @SerializedName("list")
    val list: List<T>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int
)
