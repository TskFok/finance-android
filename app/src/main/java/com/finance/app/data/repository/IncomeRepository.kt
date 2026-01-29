package com.finance.app.data.repository

import com.finance.app.data.local.dao.IncomeDao
import com.finance.app.data.local.entity.Income
import com.finance.app.data.model.PageResponse
import com.finance.app.data.remote.ApiService
import com.finance.app.data.remote.dto.CreateIncomeRequest
import com.finance.app.data.remote.dto.UpdateIncomeRequest
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IncomeRepository(
    private val apiService: ApiService,
    private val incomeDao: IncomeDao
) {
    fun getIncomes(
        page: Int = 1,
        pageSize: Int = 10,
        type: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ): Flow<Resource<PageResponse<Income>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getIncomes(page, pageSize, type, startTime, endTime)
            if (response.isSuccessful && response.body()?.code == 200) {
                val data = response.body()!!.data!!
                incomeDao.insertIncomes(data.list)
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
    
    suspend fun createIncome(
        amount: Double,
        type: String,
        incomeTime: String
    ): Resource<Income> {
        return try {
            val response = apiService.createIncome(
                CreateIncomeRequest(amount, type, incomeTime)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val income = response.body()!!.data!!
                incomeDao.insertIncome(income)
                Resource.Success(income)
            } else {
                Resource.Error(response.body()?.message ?: "创建失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun updateIncome(
        id: Int,
        amount: Double?,
        type: String?,
        incomeTime: String?
    ): Resource<Income> {
        return try {
            val response = apiService.updateIncome(
                id,
                UpdateIncomeRequest(amount, type, incomeTime)
            )
            if (response.isSuccessful && response.body()?.code == 200) {
                val income = response.body()!!.data!!
                incomeDao.updateIncome(income)
                Resource.Success(income)
            } else {
                Resource.Error(response.body()?.message ?: "更新失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
    
    suspend fun deleteIncome(id: Int): Resource<Unit> {
        return try {
            val response = apiService.deleteIncome(id)
            if (response.isSuccessful && response.body()?.code == 200) {
                val income = incomeDao.getIncomeById(id)
                income?.let { incomeDao.deleteIncome(it) }
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "删除失败")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "网络错误")
        }
    }
}
