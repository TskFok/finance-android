package com.finance.app.di

import android.content.Context
import com.finance.app.data.local.FinanceDatabase
import com.finance.app.data.local.PreferencesManager
import com.finance.app.data.remote.ApiService
import com.finance.app.data.repository.*

object AppContainer {
    private var database: FinanceDatabase? = null
    private var preferencesManager: PreferencesManager? = null
    private var apiService: ApiService? = null
    
    // Repositories
    private var authRepository: AuthRepository? = null
    private var expenseRepository: ExpenseRepository? = null
    private var incomeRepository: IncomeRepository? = null
    private var categoryRepository: CategoryRepository? = null
    private var aiRepository: AIRepository? = null
    
    fun initialize(context: Context) {
        if (database == null) {
            database = NetworkModule.provideDatabase(context)
            preferencesManager = NetworkModule.providePreferencesManager(context)
            apiService = NetworkModule.provideApiService(
                NetworkModule.provideRetrofit(
                    NetworkModule.provideOkHttpClient(preferencesManager!!),
                    NetworkModule.provideGson()
                )
            )
            
            // Initialize repositories
            authRepository = AuthRepository(apiService!!, preferencesManager!!)
            expenseRepository = ExpenseRepository(apiService!!, database!!.expenseDao())
            incomeRepository = IncomeRepository(apiService!!, database!!.incomeDao())
            categoryRepository = CategoryRepository(apiService!!, database!!.categoryDao())
            aiRepository = AIRepository(apiService!!)
        }
    }
    
    fun getAuthRepository(): AuthRepository {
        return authRepository ?: throw IllegalStateException("AppContainer not initialized")
    }
    
    fun getExpenseRepository(): ExpenseRepository {
        return expenseRepository ?: throw IllegalStateException("AppContainer not initialized")
    }
    
    fun getIncomeRepository(): IncomeRepository {
        return incomeRepository ?: throw IllegalStateException("AppContainer not initialized")
    }
    
    fun getCategoryRepository(): CategoryRepository {
        return categoryRepository ?: throw IllegalStateException("AppContainer not initialized")
    }
    
    fun getAIRepository(): AIRepository {
        return aiRepository ?: throw IllegalStateException("AppContainer not initialized")
    }
}
