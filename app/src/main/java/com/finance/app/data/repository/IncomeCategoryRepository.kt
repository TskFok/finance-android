package com.finance.app.data.repository

import com.finance.app.data.model.IncomeCategory
import com.finance.app.data.remote.ApiService
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IncomeCategoryRepository(
    private val apiService: ApiService
) {
    fun getIncomeCategories(): Flow<Resource<List<IncomeCategory>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getIncomeCategories()
            if (response.isSuccessful && response.body()?.code == 200) {
                val categories = response.body()!!.data.orEmpty()
                    .sortedBy { it.sort }
                emit(Resource.Success(categories))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
}
