package com.finance.app.data.remote

import com.finance.app.data.local.entity.Expense
import com.finance.app.data.local.entity.Income
import com.finance.app.data.model.*
import com.finance.app.data.remote.dto.*
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>
    
    @GET("api/v1/auth/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>
    
    @PUT("api/v1/auth/password")
    suspend fun changePassword(@Body request: com.finance.app.data.remote.dto.ChangePasswordRequest): Response<ApiResponse<Unit>>
    
    // Categories
    @GET("api/v1/categories")
    suspend fun getCategories(): Response<ApiResponse<List<ExpenseCategory>>>

    @GET("api/v1/income-categories")
    suspend fun getIncomeCategories(): Response<ApiResponse<List<IncomeCategory>>>
    
    // Expenses
    @GET("api/v1/expenses")
    suspend fun getExpenses(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("category") category: String? = null,
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<ApiResponse<PageResponse<Expense>>>
    
    @GET("api/v1/expenses/{id}")
    suspend fun getExpense(@Path("id") id: Int): Response<ApiResponse<Expense>>
    
    @POST("api/v1/expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<ApiResponse<Expense>>
    
    @PUT("api/v1/expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: Int,
        @Body request: UpdateExpenseRequest
    ): Response<ApiResponse<Expense>>
    
    @DELETE("api/v1/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Int): Response<ApiResponse<Unit>>
    
    @GET("api/v1/expenses/statistics")
    suspend fun getExpenseStatistics(
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<ApiResponse<ExpenseStatistics>>
    
    @GET("api/v1/expenses/detailed-statistics")
    suspend fun getDetailedStatistics(
        @Query("range_type") rangeType: String,
        @Query("year_month") yearMonth: String? = null,
        @Query("year") year: String? = null,
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null,
        @Query("categories") categories: String? = null
    ): Response<ApiResponse<ExpenseStatistics>>
    
    // Incomes
    @GET("api/v1/incomes")
    suspend fun getIncomes(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
        @Query("type") type: String? = null,
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<ApiResponse<PageResponse<Income>>>
    
    @GET("api/v1/incomes/{id}")
    suspend fun getIncome(@Path("id") id: Int): Response<ApiResponse<Income>>
    
    @POST("api/v1/incomes")
    suspend fun createIncome(@Body request: CreateIncomeRequest): Response<ApiResponse<Income>>
    
    @PUT("api/v1/incomes/{id}")
    suspend fun updateIncome(
        @Path("id") id: Int,
        @Body request: UpdateIncomeRequest
    ): Response<ApiResponse<Income>>
    
    @DELETE("api/v1/incomes/{id}")
    suspend fun deleteIncome(@Path("id") id: Int): Response<ApiResponse<Unit>>
    
    // Statistics
    @GET("api/v1/statistics/summary")
    suspend fun getSummary(
        @Query("start_time") startTime: String? = null,
        @Query("end_time") endTime: String? = null
    ): Response<ApiResponse<IncomeExpenseSummary>>
    
    // AI Models
    @GET("api/v1/ai-models")
    suspend fun getAIModels(): Response<ApiResponse<List<AIModel>>>
    
    // AI Analysis (SSE)
    @POST("api/v1/ai-analysis")
    @Streaming
    suspend fun analyzeExpenses(@Body request: AnalysisRequest): ResponseBody
    
    // AI Chat (SSE)
    @POST("api/v1/ai-chat")
    @Streaming
    suspend fun chatWithAI(@Body request: AIChatRequest): ResponseBody
    
    // AI Chat History
    @GET("api/v1/ai-chat/history")
    suspend fun getAIChatHistory(
        @Query("model_id") modelId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<ApiResponse<PageResponse<AIChatHistory>>>
    
    // AI Analysis History
    @GET("api/v1/ai-analysis/history")
    suspend fun getAIAnalysisHistory(
        @Query("model_id") modelId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<ApiResponse<PageResponse<AIAnalysisHistory>>>
}
