package com.example.expensetracker.localization

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

object LocaleManager {

    private const val PREF_LANG = "app_lang"

    val currentLanguage = mutableStateOf("en")

    fun setLanguage(context: Context, lang: String) {
        currentLanguage.value = lang

        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANG, lang).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val lang = prefs.getString(PREF_LANG, "en") ?: "en"
        currentLanguage.value = lang
        return lang
    }

    fun updateLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}