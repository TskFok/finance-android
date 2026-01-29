package com.finance.app.data.repository

import com.finance.app.data.local.dao.ExpenseDao
import com.finance.app.data.local.entity.Expense
import com.finance.app.data.model.ExpenseStatistics
import com.finance.app.data.model.IncomeExpenseSummary
import com.finance.app.data.model.PageResponse
import com.finance.app.data.remote.ApiService
import com.finance.app.data.remote.dto.CreateExpenseRequest
import com.finance.app.data.remote.dto.UpdateExpenseRequest
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExpenseRepository(
    private val apiService: ApiService,
    private val expenseDao: ExpenseDao
) {
    fun getExpenses(
        page: Int = 1,
        pageSize: Int = 10,
        category: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ): Flow<Resource<PageResponse<Expense>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getExpenses(page, pageSize, category, startTime, endTime)
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data!!
                // 缓存到本地
                expenseDao.insertExpenses(data.list)
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            // 从本地数据库读取（备用方案，当前仅返回错误）
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
    
    suspend fun createExpense(
        amount: Double,
        category: String,
        description: String?,
        expenseTime: String
    ): Resource<Expense> {
        return try {
            val response = apiService.createExpense(
                CreateExpenseRequest(amount, category, description, expenseTime)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val expense = response.body()!!.data!!
                expenseDao.insertExpense(expense)
                Resource.Success(expense)
            } else {
                Resource.Error(response.body()?.message ?: "创建失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun updateExpense(
        id: Int,
        amount: Double?,
        category: String?,
        description: String?,
        expenseTime: String?
    ): Resource<Expense> {
        return try {
            val response = apiService.updateExpense(
                id,
                UpdateExpenseRequest(amount, category, description, expenseTime)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val expense = response.body()!!.data!!
                expenseDao.updateExpense(expense)
                Resource.Success(expense)
            } else {
                Resource.Error(response.body()?.message ?: "更新失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun deleteExpense(id: Int): Resource<Unit> {
        return try {
            val response = apiService.deleteExpense(id)
            if (response.isSuccessful && response.body()?.code == 200) {
                val expense = expenseDao.getExpenseById(id)
                expense?.let { expenseDao.deleteExpense(it) }
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "删除失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun getStatistics(startTime: String?, endTime: String?): Resource<ExpenseStatistics> {
        return try {
            val response = apiService.getExpenseStatistics(startTime, endTime)
            if (response.isSuccessful && response.body()?.code == 200) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "获取统计失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun getDetailedStatistics(
        rangeType: String,
        yearMonth: String? = null,
        year: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        categories: String? = null
    ): Resource<ExpenseStatistics> {
        return try {
            val response = apiService.getDetailedStatistics(
                rangeType, yearMonth, year, startTime, endTime, categories
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "获取统计失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }

    suspend fun getIncomeExpenseSummary(startTime: String?, endTime: String?): Resource<IncomeExpenseSummary> {
        return try {
            val response = apiService.getSummary(startTime, endTime)
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data
                if (data != null) {
                    Resource.Success(data)
                } else {
                    Resource.Error("数据为空")
                }
            } else {
                Resource.Error(response.body()?.message ?: "获取失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
}
