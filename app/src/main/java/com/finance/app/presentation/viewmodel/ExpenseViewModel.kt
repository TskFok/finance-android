package com.finance.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.app.data.local.entity.Expense
import com.finance.app.data.model.ExpenseStatistics
import com.finance.app.data.model.IncomeExpenseSummary
import com.finance.app.data.model.PageResponse
import com.finance.app.data.repository.ExpenseRepository
import com.finance.app.di.AppContainer
import com.finance.app.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel : ViewModel() {
    private val expenseRepository: ExpenseRepository = AppContainer.getExpenseRepository()
    
    private val _expenses = MutableStateFlow<Resource<PageResponse<Expense>>?>(null)
    val expenses: StateFlow<Resource<PageResponse<Expense>>?> = _expenses.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _loadMoreError = MutableStateFlow<String?>(null)
    val loadMoreError: StateFlow<String?> = _loadMoreError.asStateFlow()
    
    private val _statistics = MutableStateFlow<Resource<ExpenseStatistics>>(Resource.Loading())
    val statistics: StateFlow<Resource<ExpenseStatistics>> = _statistics.asStateFlow()

    private val _summary = MutableStateFlow<Resource<IncomeExpenseSummary>?>(null)
    val summary: StateFlow<Resource<IncomeExpenseSummary>?> = _summary.asStateFlow()
    
    private var currentStartTime: String? = null
    private var currentEndTime: String? = null

    private var currentPage: Int = 1
    private var currentPageSize: Int = 10
    private var currentCategory: String? = null
    
    fun getExpenses(
        page: Int = 1,
        pageSize: Int = 10,
        category: String? = null,
        startTime: String? = null,
        endTime: String? = null
    ) {
        currentStartTime = startTime
        currentEndTime = endTime
        currentCategory = category
        currentPageSize = pageSize

        // page=1 视为刷新：重置分页状态并替换列表
        if (page <= 1) {
            currentPage = 1
            _hasMore.value = true
            _loadMoreError.value = null
        }

        loadExpensesPage(page = if (page <= 1) 1 else page, append = page > 1)
    }

    fun loadNextPage() {
        if (_isLoadingMore.value) return
        if (!_hasMore.value) return
        val current = currentPage
        // 只有已有首屏数据时才加载更多
        if (_expenses.value !is Resource.Success) return
        loadExpensesPage(page = current + 1, append = true)
    }

    private fun loadExpensesPage(page: Int, append: Boolean) {
        viewModelScope.launch {
            expenseRepository.getExpenses(
                page = page,
                pageSize = currentPageSize,
                category = currentCategory,
                startTime = currentStartTime,
                endTime = currentEndTime
            ).collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        if (append) {
                            _isLoadingMore.value = true
                        } else {
                            _expenses.value = res
                        }
                    }
                    is Resource.Success -> {
                        val pageData = res.data
                        if (pageData != null) {
                            val oldList = if (append) {
                                (_expenses.value as? Resource.Success)?.data?.list.orEmpty()
                            } else {
                                emptyList()
                            }
                            val mergedList = oldList + pageData.list
                            val merged = pageData.copy(list = mergedList)
                            _expenses.value = Resource.Success(merged)

                            currentPage = pageData.page
                            currentPageSize = pageData.pageSize
                            _hasMore.value = (pageData.page * pageData.pageSize) < pageData.total
                            _loadMoreError.value = null
                        } else if (!append) {
                            _expenses.value = Resource.Error("获取失败")
                        }
                        _isLoadingMore.value = false
                    }
                    is Resource.Error -> {
                        if (append) {
                            _isLoadingMore.value = false
                            _loadMoreError.value = res.message ?: "加载失败"
                        } else {
                            _expenses.value = res
                        }
                    }
                }
            }
        }
    }
    
    fun refreshExpenses() {
        getExpenses(
            page = 1,
            pageSize = currentPageSize,
            category = currentCategory,
            startTime = currentStartTime,
            endTime = currentEndTime
        )
    }
    
    fun createExpense(
        amount: Double,
        category: String,
        description: String?,
        expenseTime: String
    ) {
        viewModelScope.launch {
            val result = expenseRepository.createExpense(amount, category, description, expenseTime)
            if (result is Resource.Success) {
                // 重置分页状态并刷新列表，确保新添加的记录能显示
                currentPage = 1
                _hasMore.value = true
                _loadMoreError.value = null
                refreshExpenses() // 使用当前的时间筛选刷新列表
            }
        }
    }
    
    fun updateExpense(
        id: Int,
        amount: Double?,
        category: String?,
        description: String?,
        expenseTime: String?
    ) {
        viewModelScope.launch {
            val result = expenseRepository.updateExpense(id, amount, category, description, expenseTime)
            if (result is Resource.Success) {
                refreshExpenses() // 使用当前的时间筛选刷新列表
            }
        }
    }
    
    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            val result = expenseRepository.deleteExpense(id)
            if (result is Resource.Success) {
                refreshExpenses() // 使用当前的时间筛选刷新列表
            }
        }
    }
    
    fun getStatistics(startTime: String?, endTime: String?) {
        viewModelScope.launch {
            _statistics.value = expenseRepository.getStatistics(startTime, endTime)
        }
    }

    fun getIncomeExpenseSummary(startTime: String?, endTime: String?) {
        viewModelScope.launch {
            _summary.value = expenseRepository.getIncomeExpenseSummary(startTime, endTime)
        }
    }
    
    fun getDetailedStatistics(
        rangeType: String,
        yearMonth: String? = null,
        year: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        categories: String? = null
    ) {
        viewModelScope.launch {
            _statistics.value = expenseRepository.getDetailedStatistics(
                rangeType, yearMonth, year, startTime, endTime, categories
            )
        }
    }
}
