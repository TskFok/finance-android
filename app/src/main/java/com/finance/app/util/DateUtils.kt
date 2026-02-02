package com.finance.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * 获取本月的开始时间（格式：yyyy-MM-dd）
     */
    fun getCurrentMonthStart(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return dateFormat.format(calendar.time)
    }
    
    /**
     * 获取本月的结束时间（格式：yyyy-MM-dd）
     */
    fun getCurrentMonthEnd(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return dateFormat.format(calendar.time)
    }
    
    /**
     * 获取当前时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    fun getCurrentDateTime(): String {
        return dateTimeFormat.format(Date())
    }
    
    /**
     * 格式化日期（格式：yyyy-MM-dd）
     */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    /**
     * 格式化日期时间为年月日时分秒（格式：yyyy-MM-dd HH:mm:ss）
     * 支持解析 yyyy-MM-dd、yyyy-MM-dd HH:mm:ss、ISO 8601 等常见格式
     */
    fun formatDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrBlank()) return "—"
        return try {
            val date = parseDateTime(dateTimeString)
            if (date != null) dateTimeFormat.format(date) else dateTimeString
        } catch (e: Exception) {
            dateTimeString
        }
    }
    
    /**
     * 解析日期时间字符串，支持多种格式
     */
    private fun parseDateTime(dateTimeString: String): Date? {
        val trimmed = dateTimeString.trim()
        val formatters = listOf(
            dateTimeFormat,  // yyyy-MM-dd HH:mm:ss
            dateFormat,     // yyyy-MM-dd
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        )
        for (formatter in formatters) {
            try {
                formatter.parse(trimmed)?.let { return it }
            } catch (_: Exception) { }
        }
        return null
    }
    
    /**
     * 解析日期字符串
     */
    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取当前年月（格式：yyyy-MM）
     */
    fun getCurrentYearMonth(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return String.format("%04d-%02d", year, month)
    }
}
