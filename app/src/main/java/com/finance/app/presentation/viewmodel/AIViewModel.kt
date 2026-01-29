package com.finance.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.app.data.model.AIChatHistory
import com.finance.app.data.model.AIAnalysisHistory
import com.finance.app.data.model.AIModel
import com.finance.app.data.model.PageResponse
import com.finance.app.data.repository.AIRepository
import com.finance.app.di.AppContainer
import com.finance.app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AIViewModel : ViewModel() {
    private val aiRepository: AIRepository = AppContainer.getAIRepository()
    
    private val _aiModels = MutableStateFlow<Resource<List<AIModel>>?>(null)
    val aiModels: StateFlow<Resource<List<AIModel>>?> = _aiModels.asStateFlow()
    
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()
    
    private val _isAnalyzing = MutableStateFlow<Boolean>(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    
    private val _chatHistory = MutableStateFlow<Resource<PageResponse<AIChatHistory>>?>(null)
    val chatHistory: StateFlow<Resource<PageResponse<AIChatHistory>>?> = _chatHistory.asStateFlow()
    
    private val _analysisHistory = MutableStateFlow<Resource<PageResponse<AIAnalysisHistory>>?>(null)
    val analysisHistory: StateFlow<Resource<PageResponse<AIAnalysisHistory>>?> = _analysisHistory.asStateFlow()
    
    private val _chatMessages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatMessages: StateFlow<List<Pair<String, String>>> = _chatMessages.asStateFlow()
    
    fun getAIModels() {
        viewModelScope.launch {
            aiRepository.getAIModels().collect {
                _aiModels.value = it
            }
        }
    }
    
    fun analyzeExpenses(
        modelId: Int,
        startTime: String,
        endTime: String,
        userId: Int? = null
    ) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _aiResponse.value = ""
            try {
                val result = aiRepository.analyzeExpenses(modelId, startTime, endTime, userId) { type, content ->
                    // 回调已经在协程中，直接更新状态
                    when (type) {
                        "delta" -> {
                            // 实时更新响应内容
                            _aiResponse.value += content ?: ""
                        }
                        "done" -> {
                            _isAnalyzing.value = false
                            // 确保 done 事件也被处理
                            if (_aiResponse.value.isEmpty()) {
                                _aiResponse.value = "分析完成"
                            }
                        }
                        "error" -> {
                            _isAnalyzing.value = false
                            val errorMsg = content ?: "未知错误"
                            if (_aiResponse.value.isEmpty()) {
                                _aiResponse.value = "错误: $errorMsg"
                            } else {
                                _aiResponse.value += "\n错误: $errorMsg"
                            }
                        }
                    }
                }
                if (result is Resource.Error) {
                    _isAnalyzing.value = false
                    if (_aiResponse.value.isEmpty()) {
                        _aiResponse.value = "分析失败: ${result.message}"
                    }
                } else if (_isAnalyzing.value) {
                    // 如果没有收到 done 事件，但函数返回了，手动结束
                    _isAnalyzing.value = false
                }
            } catch (e: Exception) {
                _isAnalyzing.value = false
                _aiResponse.value = "分析失败: ${e.message ?: "未知错误"}"
                e.printStackTrace()
            }
        }
    }
    
    fun chatWithAI(modelId: Int, message: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _aiResponse.value = ""
            
            // 添加用户消息
            val currentMessages = _chatMessages.value.toMutableList()
            currentMessages.add(Pair("user", message))
            _chatMessages.value = currentMessages
            
            try {
                val result = aiRepository.chatWithAI(modelId, message) { type, content ->
                    // 回调已经在协程中，直接更新状态
                    when (type) {
                        "delta" -> {
                            _aiResponse.value += content ?: ""
                        }
                        "done" -> {
                            // 先添加 AI 回复到消息列表，确保内容不会丢失
                            val responseContent = _aiResponse.value
                            if (responseContent.isNotEmpty()) {
                                val updatedMessages = _chatMessages.value.toMutableList()
                                updatedMessages.add(Pair("ai", responseContent))
                                _chatMessages.value = updatedMessages
                            }
                            // 然后清空响应和停止分析状态
                            _aiResponse.value = ""
                            _isAnalyzing.value = false
                            // 刷新历史记录
                            getChatHistory(modelId)
                        }
                        "error" -> {
                            val errorMsg = content ?: "未知错误"
                            val errorContent = if (_aiResponse.value.isNotEmpty()) {
                                "${_aiResponse.value}\n错误: $errorMsg"
                            } else {
                                "错误: $errorMsg"
                            }
                            // 先添加错误消息到列表
                            val updatedMessages = _chatMessages.value.toMutableList()
                            updatedMessages.add(Pair("ai", errorContent))
                            _chatMessages.value = updatedMessages
                            // 然后清空响应和停止分析状态
                            _aiResponse.value = ""
                            _isAnalyzing.value = false
                        }
                    }
                }
                if (result is Resource.Error) {
                    _isAnalyzing.value = false
                    if (_aiResponse.value.isEmpty()) {
                        val errorMsg = result.message ?: "聊天失败"
                        val updatedMessages = _chatMessages.value.toMutableList()
                        updatedMessages.add(Pair("ai", "错误: $errorMsg"))
                        _chatMessages.value = updatedMessages
                    }
                }
            } catch (e: Exception) {
                _isAnalyzing.value = false
                val errorMsg = e.message ?: "聊天失败"
                val updatedMessages = _chatMessages.value.toMutableList()
                updatedMessages.add(Pair("ai", "错误: $errorMsg"))
                _chatMessages.value = updatedMessages
            }
        }
    }
    
    fun getChatHistory(modelId: Int, page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            aiRepository.getAIChatHistory(modelId, page, pageSize).collect {
                _chatHistory.value = it
            }
        }
    }
    
    fun getAnalysisHistory(modelId: Int, page: Int = 1, pageSize: Int = 20) {
        viewModelScope.launch {
            aiRepository.getAIAnalysisHistory(modelId, page, pageSize).collect {
                _analysisHistory.value = it
            }
        }
    }
    
    fun clearChat() {
        _chatMessages.value = emptyList()
        _aiResponse.value = ""
    }
}
