package com.autoaccounting.common.prefs

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var username: String
        get() = prefs.getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    companion object {
        private const val KEY_USERNAME = "username"
        private const val DEFAULT_USERNAME = "用户"
    }
}
