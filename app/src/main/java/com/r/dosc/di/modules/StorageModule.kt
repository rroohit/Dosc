package com.r.dosc.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.r.dosc.data.preference.AppPreferenceStorage
import com.r.dosc.data.preference.PreferenceStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val SETTINGS_PREFERENCE = "setting_preference"

@InstallIn(SingletonComponent::class)
@Module
object StorageModule {

    @Singleton
    @Provides
    fun providePreference(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(SETTINGS_PREFERENCE) }
        )
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class StoragePrefModule {
    @Binds
    abstract fun providePreferenceStorage(
        appPreferenceStorage: AppPreferenceStorage
    ): PreferenceStorage

}