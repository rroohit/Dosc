package com.r.dosc.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceStorage
@Inject
constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceStorage {

    private object PreferencesKeys {
        val SORT_TYPE_ID = intPreferencesKey("pref_sort_id")
        val IS_DARK_THEME = booleanPreferencesKey("pref_dark_theme")
        val IS_START_WITH_FILE_NAME = booleanPreferencesKey("pref_start_with_file_name")

    }

    override val sortTypeId: Flow<Int>
        get() = dataStore.getValueAsFlow(PreferencesKeys.SORT_TYPE_ID, 1)

    override val isDarkTheme: Flow<Boolean>
        get() = dataStore.getValueAsFlow(PreferencesKeys.IS_DARK_THEME, false)

    override val isStartWithFileName: Flow<Boolean>
        get() = dataStore.getValueAsFlow(PreferencesKeys.IS_START_WITH_FILE_NAME, true)

    override suspend fun setSortId(sortId: Int) {
        dataStore.setValue(PreferencesKeys.SORT_TYPE_ID, sortId)

    }

    override suspend fun setIsDarkTheme(isDarkTheme: Boolean) {
        dataStore.setValue(PreferencesKeys.IS_DARK_THEME, isDarkTheme)
    }

    override suspend fun setIsStartWithFileName(isStartWithFileName: Boolean) {
        dataStore.setValue(PreferencesKeys.IS_START_WITH_FILE_NAME, isStartWithFileName)
    }

    override suspend fun clearPreferenceStorage() {
        dataStore.edit {
            it.clear()
        }
    }

}

private suspend fun <T> DataStore<Preferences>.setValue(
    key: Preferences.Key<T>,
    value: T
) {
    this.edit { preferences ->
        preferences[key] = value
    }
}

private fun <T> DataStore<Preferences>.getValueAsFlow(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> {
    return this.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[key] ?: defaultValue

    }
}

interface PreferenceStorage {

    val sortTypeId: Flow<Int>
    val isDarkTheme: Flow<Boolean>
    val isStartWithFileName: Flow<Boolean>

    suspend fun setSortId(sortId: Int)

    suspend fun setIsDarkTheme(isDarkTheme: Boolean)

    suspend fun setIsStartWithFileName(isStartWithFileName: Boolean)

    suspend fun clearPreferenceStorage()
}