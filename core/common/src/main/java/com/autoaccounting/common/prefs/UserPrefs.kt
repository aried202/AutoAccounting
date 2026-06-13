package com.autoaccounting.common.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * 使用 Jetpack DataStore 替代 SharedPreferences 实现的用户偏好管理。
 * - 提供基于 Flow 的响应式读取
 * - 写入操作挂起以保证事务性
 */
@Singleton
class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    /** 用户名的响应式订阅入口，初值为 [DEFAULT_USERNAME]。 */
    val usernameFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_USERNAME] ?: DEFAULT_USERNAME
    }

    /** 同步（挂起）读取当前用户名。 */
    suspend fun currentUsername(): String = usernameFlow.first()

    suspend fun setUsername(name: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USERNAME] = name
        }
    }

    companion object {
        private const val DEFAULT_USERNAME = "用户"
        private val KEY_USERNAME = stringPreferencesKey("username")
    }
}
