package com.garan.tempo

import android.content.Context
import androidx.health.services.client.HealthServices
import com.garan.tempo.settings.TempoSettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProviderModule {
    @Singleton
    @Provides
    fun provideHpService(@ApplicationContext appContext: Context) =
        HealthServices.getClient(appContext)

    @Singleton
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    fun provideTempoSettings(@ApplicationContext appContext: Context) =
        TempoSettingsManager(appContext)
}
