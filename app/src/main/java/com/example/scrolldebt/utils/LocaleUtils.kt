package com.example.scrolldebt.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Helpers for the in-app language setting.
 *
 * The app deliberately does *not* follow the system locale: the user picks a language
 * inside ScrollDebt and every surface (Activity UI, notifications, widget, shared image)
 * has to honour that choice. Android resolves `strings.xml` against the resources of the
 * [Context] it is asked through, so anything that renders user-facing text must go through
 * a context produced by [withAppLocale] rather than the raw application context.
 */
object LocaleUtils {

    /** Languages shipped with a full `values-XX/strings.xml`. */
    val SUPPORTED_LANGUAGES = listOf("en", "es", "fr", "de", "pl")

    /**
     * Returns a copy of this context whose resources resolve against [language].
     *
     * Falls back to the receiver unchanged when the language is not one we ship, so a
     * corrupted preference can never leave the UI without strings.
     */
    fun withAppLocale(context: Context, language: String): Context {
        val lang = language.lowercase()
        if (lang !in SUPPORTED_LANGUAGES) return context

        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
