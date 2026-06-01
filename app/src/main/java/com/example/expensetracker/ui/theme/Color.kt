package com.example.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

val PurpleGrey80 = Color(0xFFBFDBFE)
val Pink80 = Color(0xFFDBEAFE)

val PurpleGrey40 = Color(0xFF3B82F6)
val Pink40 = Color(0xFF60A5FA)

val Zinc = Color(0xFF2563EB)
val LightGrey = Color(0xFF64748B)

val Navy = Color(0xFF0F172A)
val Navy80 = Color(0xCCFFFFFF)

val Teal = Color(0xFF3B82F6)
val Teal80 = Color(0xCCFFFFFF)

val Indigo = Color(0xFF1D4ED8)
val Indigo80 = Color(0xCCFFFFFF)

val Amethyst = Color(0xFF60A5FA)
val Amethyst80 = Color(0xCCFFFFFF)

val White = Color(0xFFFFFFFF)
val White80 = Color(0xCCFFFFFF)

val Red = Color(0xFFEF4444)
val Green = Color(0xFF22C55E)

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val text: Color
) {
    data object Night : ThemeColors(
        background = Navy,
        surface = Teal,
        primary = Zinc,
        secondary = Indigo,
        tertiary = Amethyst,
        text = White
    )

    data object Day : ThemeColors(
        background = Color.White,
        surface = Color.White,
        primary = Zinc,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        text = Color.Black
    )
}