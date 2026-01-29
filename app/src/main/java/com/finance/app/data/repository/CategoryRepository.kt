package com.finance.app.data.repository

import com.finance.app.data.local.dao.CategoryDao
import com.finance.app.data.model.ExpenseCategory
import com.finance.app.data.remote.ApiService
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoryRepository(
    private val apiService: ApiService,
    private val categoryDao: CategoryDao
) {
    fun getCategories(): Flow<Resource<List<ExpenseCategory>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body()?.code == 200) {
                val categories = response.body()!!.data!!
                categoryDao.insertCategories(categories)
                emit(Resource.Success(categories))
            } else {
                // 从本地读取（备用方案，当前仅返回错误）
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            // 从本地读取（备用方案，当前仅返回错误）
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
}
