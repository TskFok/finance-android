package com.finance.app.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Titanium 风格配色 - 参考 screen-2.html 的深色科技感设计
 */
object TitaniumColors {
    // 背景色
    val Background = Color(0xFF050505)
    val Surface = Color(0xFF0F0F0F)
    val SurfaceVariant = Color(0xFF161616)
    val SurfaceElevated = Color(0xFF1A1A1A)

    // 边框
    val Border = Color(0xFF222222)
    val BorderVariant = Color(0xFF333333)

    // 文字
    val TextPrimary = Color(0xFFF3F4F6)      // gray-100
    val TextSecondary = Color(0xFFE5E7EB)    // gray-200
    val TextTertiary = Color(0xFFD1D5DB)      // gray-300
    val TextMuted = Color(0xFF6B7280)         // gray-500
    val TextDim = Color(0xFF4B5563)           // gray-600

    // 强调色
    val Positive = Color(0xFF4ADE80)         // green-400
    val PositiveContainer = Color(0xFF1A2E1A)
    val PositiveBorder = Color(0xFF2A4A2A)
    val Negative = Color(0xFFEF4444)        // red-500
    val NegativeContainer = Color(0xFF2E1A1A)
    val NegativeBorder = Color(0xFF4A2A2A)
    // 金属质感高亮（Swap 按钮等）
    val MetalAccent = Color(0xFFF5F5F5)
    val MetalShimmer = Color(0x1AFFFFFF)

    // 其他
    val Error = Color(0xFFEF4444)
    val Outline = Color(0xFF333333)
}
