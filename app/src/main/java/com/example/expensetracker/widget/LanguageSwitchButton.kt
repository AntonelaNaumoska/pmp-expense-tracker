package com.example.expensetracker.widget

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.expensetracker.localization.LocaleManager

@Composable
fun LanguageSwitchButton() {

    val context = androidx.compose.ui.platform.LocalContext.current
    val lang = LocaleManager.currentLanguage.value

    Button(onClick = {

        val newLang = if (lang == "en") "mk" else "en"

        LocaleManager.setLanguage(context, newLang)

        // FORCE FULL UI REFRESH
        (context as android.app.Activity).recreate()

    }) {
        Text(if (lang == "en") "MK" else "EN")
    }
}