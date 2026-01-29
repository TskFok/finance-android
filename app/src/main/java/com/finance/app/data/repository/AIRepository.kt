package com.finance.app.data.repository

import com.finance.app.data.model.AIChatHistory
import com.finance.app.data.model.AIAnalysisHistory
import com.finance.app.data.model.AIModel
import com.finance.app.data.model.PageResponse
import com.finance.app.data.remote.ApiService
import com.finance.app.data.remote.dto.AIChatRequest
import com.finance.app.data.remote.dto.AnalysisRequest
import com.finance.app.util.Resource
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import okio.buffer
import okio.source
import okio.BufferedSource

class AIRepository(
    private val apiService: ApiService
) {
    fun getAIModels(): Flow<Resource<List<AIModel>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAIModels()
            if (response.isSuccessful && response.body()?.code == 200) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
    
    suspend fun analyzeExpenses(
        modelId: Int,
        startTime: String,
        endTime: String,
        userId: Int? = null,
        onEvent: (String, String?) -> Unit
    ): Resource<Unit> {
        return try {
            val request = AnalysisRequest(modelId, startTime, endTime, userId)
            val responseBody = apiService.analyzeExpenses(request)
            // 在 IO 线程处理 SSE 流
            withContext(Dispatchers.IO) {
                parseSSEResponse(responseBody, onEvent)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            onEvent("error", e.message ?: "分析失败")
            Resource.Error(e.message ?: "分析失败")
        }
    }
    
    suspend fun chatWithAI(
        modelId: Int,
        message: String,
        onEvent: (String, String?) -> Unit
    ): Resource<Unit> {
        return try {
            val request = AIChatRequest(modelId, message)
            val responseBody = apiService.chatWithAI(request)
            // 在 IO 线程处理 SSE 流
            withContext(Dispatchers.IO) {
                parseSSEResponse(responseBody, onEvent)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            onEvent("error", e.message ?: "聊天失败")
            Resource.Error(e.message ?: "聊天失败")
        }
    }
    
    fun getAIChatHistory(
        modelId: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Resource<PageResponse<AIChatHistory>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAIChatHistory(modelId, page, pageSize)
            if (response.isSuccessful && response.body()?.code == 200) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
    
    fun getAIAnalysisHistory(
        modelId: Int,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Resource<PageResponse<AIAnalysisHistory>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAIAnalysisHistory(modelId, page, pageSize)
            if (response.isSuccessful && response.body()?.code == 200) {
                emit(Resource.Success(response.body()!!.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "获取失败"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "网络错误"))
        }
    }
    
    private suspend fun parseSSEResponse(
        responseBody: okhttp3.ResponseBody,
        onEvent: (String, String?) -> Unit
    ) {
        // 解析 SSE 流
        var lineCount = 0
        var hasReceivedData = false
        try {
            // 使用 byteStream() 而不是 source()，确保流式读取
            responseBody.byteStream().bufferedReader(Charsets.UTF_8).use { reader ->
                Log.d("AIRepository", "开始解析 SSE 流")
                while (true) {
                    val line = reader.readLine() ?: break
                    lineCount++
                    
                    Log.d("AIRepository", "读取第 $lineCount 行: ${line.take(150)}")
                    
                    when {
                        line.startsWith("data: ") -> {
                            val data = line.substring(6).trim()
                            if (data.isNotEmpty()) {
                                hasReceivedData = true
                                Log.d("AIRepository", "提取到数据: $data")
                                // 立即处理数据，不等待空行
                                processSSEData(data, onEvent)
                            }
                        }
                        line.isBlank() -> {
                            // 空行，忽略（数据已经在遇到 data: 时处理了）
                            Log.d("AIRepository", "遇到空行")
                        }
                        line.startsWith("event: ") -> {
                            val eventType = line.substring(7).trim()
                            Log.d("AIRepository", "收到事件类型: $eventType")
                            if (eventType == "error") {
                                onEvent("error", "服务器返回错误事件")
                                break
                            }
                        }
                        line.startsWith("id: ") || line.startsWith("retry: ") -> {
                            // 忽略 id 和 retry 字段
                        }
                        else -> {
                            // 如果行不是标准 SSE 格式，尝试直接处理
                            Log.d("AIRepository", "非标准 SSE 行，尝试处理: ${line.take(100)}")
                            if (line.trim().isNotEmpty()) {
                                hasReceivedData = true
                                processSSEData(line.trim(), onEvent)
                            }
                        }
                    }
                }
                
                Log.d("AIRepository", "SSE 流读取完成，共 $lineCount 行，收到数据: $hasReceivedData")
                
                if (!hasReceivedData) {
                    Log.w("AIRepository", "没有收到任何数据")
                    onEvent("error", "没有收到任何数据")
                }
            }
        } catch (e: Exception) {
            Log.e("AIRepository", "解析 SSE 响应失败", e)
            e.printStackTrace()
            onEvent("error", e.message ?: "解析响应失败: ${e.javaClass.simpleName}")
        }
    }
    
    private fun processSSEData(json: String, onEvent: (String, String?) -> Unit) {
        val trimmedJson = json.trim()
        if (trimmedJson.isEmpty()) {
            Log.d("AIRepository", "数据为空，跳过")
            return
        }
        
        Log.d("AIRepository", "处理数据: $trimmedJson")
        
        try {
            val event = com.google.gson.Gson().fromJson(
                trimmedJson,
                com.finance.app.data.remote.dto.SSEEvent::class.java
            )
            Log.d("AIRepository", "解析成功: type=${event.type}, content=${event.content?.take(100)}")
            // 确保回调被调用
            onEvent(event.type, event.content)
            Log.d("AIRepository", "回调已调用: type=${event.type}")
        } catch (e: Exception) {
            Log.e("AIRepository", "JSON 解析失败: ${e.message}", e)
            Log.e("AIRepository", "原始数据: $trimmedJson")
            // 如果 JSON 解析失败，尝试作为纯文本处理
            if (trimmedJson.startsWith("{") && trimmedJson.endsWith("}")) {
                // 看起来像 JSON，但解析失败
                onEvent("error", "JSON 解析失败: ${e.message}")
            } else {
                // 纯文本内容，作为 delta 事件处理
                Log.d("AIRepository", "作为纯文本处理: ${trimmedJson.take(50)}")
                onEvent("delta", trimmedJson)
            }
        }
    }
}
