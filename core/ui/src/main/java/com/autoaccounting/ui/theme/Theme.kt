package com.autoaccounting.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = CardWhite,
    primaryContainer = PrimaryGreenLight,
    secondary = ExpenseRed,
    secondaryContainer = ExpenseRedLight,
    tertiary = BalanceBlue,
    background = PageBackground,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    surfaceVariant = PageBackground
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = CardWhite,
    primaryContainer = PrimaryGreenLight,
    secondary = ExpenseRed,
    secondaryContainer = ExpenseRedLight,
    tertiary = BalanceBlue
)

@Composable
fun AutoAccountingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
