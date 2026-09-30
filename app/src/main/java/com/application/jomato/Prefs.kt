package com.application.jomato

import android.content.Context
import android.content.SharedPreferences
import com.application.jomato.utils.FileLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object Prefs {

    private const val PREFS_NAME = "jomato_prefs"
    private const val TAG = "Prefs"
    private const val KEY_INSTALL_ID = "install_id"
    private const val KEY_HIDE_INTEGRITY = "hide_integrity_dialog"
    private const val KEY_THEME_MODE = "theme_mode"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getInstallId(context: Context): String {
        var installId = prefs(context).getString(KEY_INSTALL_ID, null)
        if (installId == null) {
            val bytes = ByteArray(8)
            java.security.SecureRandom().nextBytes(bytes)
            installId = bytes.joinToString("") { "%02x".format(it) }
            prefs(context).edit().putString(KEY_INSTALL_ID, installId).apply()
            FileLogger.log(context, TAG, "Generated new install ID: $installId")
        }
        return installId!!
    }

    fun getHideIntegrity(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HIDE_INTEGRITY, false)

    fun setHideIntegrity(context: Context, hide: Boolean) {
        prefs(context).edit().putBoolean(KEY_HIDE_INTEGRITY, hide).apply()
    }

    /** "system" | "dark" | "light" */
    private val _themeMode = MutableStateFlow("system")
    val themeMode = _themeMode.asStateFlow()

    fun loadThemeMode(context: Context) {
        _themeMode.value = prefs(context).getString(KEY_THEME_MODE, "system") ?: "system"
    }

    private const val KEY_ENABLE_APP_ANALYTICS = "enable_app_analytics"
    private const val KEY_ENABLE_ORDER_TRACKING = "enable_order_tracking"
    private const val KEY_ENABLE_ORDER_TELEMETRY = "enable_order_telemetry"
    private const val KEY_PRIVACY_ONBOARDING_COMPLETED = "privacy_onboarding_completed"

    /** Anonymous App Open Analytics (Default: false / Opt-in) */
    fun isAppAnalyticsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLE_APP_ANALYTICS, false)

    fun setAppAnalyticsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLE_APP_ANALYTICS, enabled).apply()
    }

    /** Consent for background querying of order summaries to calculate savings (Default: false / Opt-in) */
    fun isOrderTrackingConsentGranted(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLE_ORDER_TRACKING, false)

    fun setOrderTrackingConsent(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLE_ORDER_TRACKING, enabled).apply()
    }

    /** Consent for sharing order savings and Order IDs with external metrics worker (Default: false / Opt-in) */
    fun isOrderTelemetryEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLE_ORDER_TELEMETRY, false)

    fun setOrderTelemetryEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLE_ORDER_TELEMETRY, enabled).apply()
    }

    /** Whether the user has seen and completed the initial privacy onboarding notice */
    fun isPrivacyOnboardingCompleted(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PRIVACY_ONBOARDING_COMPLETED, false)

    fun setPrivacyOnboardingCompleted(context: Context, completed: Boolean) {
        prefs(context).edit().putBoolean(KEY_PRIVACY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun cycleThemeMode(context: Context) {
        val next = when (_themeMode.value) {
            "system" -> "dark"
            "dark" -> "light"
            else -> "system"
        }
        _themeMode.value = next
        prefs(context).edit().putString(KEY_THEME_MODE, next).apply()
    }

}