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
