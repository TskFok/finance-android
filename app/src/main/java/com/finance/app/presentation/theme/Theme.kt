package com.finance.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat

/**
 * Titanium 风格配色方案 - 参考 screen-2.html 深色科技感设计
 * 背景 #050505，卡片 #0f0f0f，边框 #222，正数绿 #4ade80
 */
private val TitaniumDarkColorScheme = darkColorScheme(
    primary = TitaniumColors.TextPrimary,
    onPrimary = TitaniumColors.Background,
    primaryContainer = TitaniumColors.SurfaceVariant,
    onPrimaryContainer = TitaniumColors.TextSecondary,
    secondary = TitaniumColors.Positive,
    onSecondary = TitaniumColors.Background,
    tertiary = TitaniumColors.MetalAccent,
    onTertiary = TitaniumColors.Background,
    background = TitaniumColors.Background,
    onBackground = TitaniumColors.TextPrimary,
    surface = TitaniumColors.Surface,
    onSurface = TitaniumColors.TextSecondary,
    surfaceVariant = TitaniumColors.SurfaceVariant,
    onSurfaceVariant = TitaniumColors.TextMuted,
    outline = TitaniumColors.Border,
    outlineVariant = TitaniumColors.BorderVariant,
    error = TitaniumColors.Error,
    onError = TitaniumColors.TextPrimary
)

/**
 * 浅色模式 - 保持 Titanium 品牌色调的浅色变体
 */
private val TitaniumLightColorScheme = lightColorScheme(
    primary = Color(0xFF1F2937),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE5E7EB),
    onPrimaryContainer = Color(0xFF111827),
    secondary = Color(0xFF059669),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF374151),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun FinanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TitaniumDarkColorScheme else TitaniumLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(
            displayLarge = androidx.compose.material3.Typography().displayLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            displayMedium = androidx.compose.material3.Typography().displayMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            headlineMedium = androidx.compose.material3.Typography().headlineMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            titleMedium = androidx.compose.material3.Typography().titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            labelSmall = androidx.compose.material3.Typography().labelSmall.copy(
                fontWeight = FontWeight.Medium
            )
        ),
        content = content
    )
}
