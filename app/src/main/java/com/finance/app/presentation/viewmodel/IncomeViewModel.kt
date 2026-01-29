package com.finance.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.app.data.local.entity.Income
import com.finance.app.data.model.PageResponse
import com.finance.app.data.repository.IncomeRepository
import com.finance.app.di.AppContainer
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeViewModel : ViewModel() {
    private val incomeRepository: IncomeRepository = AppContainer.getIncomeRepository()
    
    private val _incomes = MutableStateFlow<Resource<PageResponse<Income>>?>(null)
    val incomes: StateFlow<Resource<PageResponse<Income>>?> = _incomes.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _loadMoreError = MutableStateFlow<String?>(null)
    val loadMoreError: StateFlow<String?> = _loadMoreError.asStateFlow()
    
    private var currentStartTime: String? = null
    private var currentEndTime: String? = null

    private var currentPage: Int = 1
    private var currentPageSize: Int = 10
    private var currentType: String? = null
    
    fun getIncomes(
        page: Int = 1,
        pageSize: Int = 10,
        type: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ) {
        currentStartTime = startTime
        currentEndTime = endTime
        currentType = type
        currentPageSize = pageSize

        if (page <= 1) {
            currentPage = 1
            _hasMore.value = true
            _loadMoreError.value = null
        }

        loadIncomesPage(page = if (page <= 1) 1 else page, append = page > 1)
    }

    fun loadNextPage() {
        if (_isLoadingMore.value) return
        if (!_hasMore.value) return
        if (_incomes.value !is Resource.Success) return
        loadIncomesPage(page = currentPage + 1, append = true)
    }

    private fun loadIncomesPage(page: Int, append: Boolean) {
        viewModelScope.launch {
            incomeRepository.getIncomes(
                page = page,
                pageSize = currentPageSize,
                type = currentType,
                startTime = currentStartTime,
                endTime = currentEndTime
            ).collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        if (append) {
                            _isLoadingMore.value = true
                        } else {
                            _incomes.value = res
                        }
                    }
                    is Resource.Success -> {
                        val pageData = res.data
                        if (pageData != null) {
                            val oldList = if (append) {
                                (_incomes.value as? Resource.Success)?.data?.list.orEmpty()
                            } else {
                                emptyList()
                            }
                            val mergedList = oldList + pageData.list
                            val merged = pageData.copy(list = mergedList)
                            _incomes.value = Resource.Success(merged)

                            currentPage = pageData.page
                            currentPageSize = pageData.pageSize
                            _hasMore.value = (pageData.page * pageData.pageSize) < pageData.total
                            _loadMoreError.value = null
                        } else if (!append) {
                            _incomes.value = Resource.Error("获取失败")
                        }
                        _isLoadingMore.value = false
                    }
                    is Resource.Error -> {
                        if (append) {
                            _isLoadingMore.value = false
                            _loadMoreError.value = res.message ?: "加载失败"
                        } else {
                            _incomes.value = res
                        }
                    }
                }
            }
        }
    }
    
    fun refreshIncomes() {
        getIncomes(
            page = 1,
            pageSize = currentPageSize,
            type = currentType,
            startTime = currentStartTime,
            endTime = currentEndTime
        )
    }
    
    fun createIncome(
        amount: Double,
        type: String,
        incomeTime: String
    ) {
        viewModelScope.launch {
            val result = incomeRepository.createIncome(amount, type, incomeTime)
            if (result is Resource.Success) {
                refreshIncomes() // 使用当前的时间筛选刷新列表
            }
        }
    }
    
    fun updateIncome(
        id: Int,
        amount: Double?,
        type: String?,
        incomeTime: String?
    ) {
        viewModelScope.launch {
            val result = incomeRepository.updateIncome(id, amount, type, incomeTime)
            if (result is Resource.Success) {
                refreshIncomes() // 使用当前的时间筛选刷新列表
            }
        }
    }
    
    fun deleteIncome(id: Int) {
        viewModelScope.launch {
            val result = incomeRepository.deleteIncome(id)
            if (result is Resource.Success) {
                refreshIncomes() // 使用当前的时间筛选刷新列表
            }
        }
    }
}
