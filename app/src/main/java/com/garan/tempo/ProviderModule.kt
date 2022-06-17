package com.garan.tempo

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.ExerciseCapabilities
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.garan.tempo.settings.ExerciseSettingsDao
import com.garan.tempo.settings.ExerciseSettingsDatabase
import com.garan.tempo.settings.defaults.defaultExerciseSettingsList
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import javax.inject.Provider
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
    fun provideCapabilities(
        healthProvider: Provider<HealthServicesClient>,
        scopeProvider: Provider<CoroutineScope>
    ): Deferred<ExerciseCapabilities> {
        val client = healthProvider.get()
        return scopeProvider.get().async {
            client.exerciseClient.capabilities.await()
        }
    }

    @Singleton
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Singleton
    @Provides
    fun provideTempoDatabase(
        @ApplicationContext appContext: Context,
        provider: Provider<ExerciseSettingsDao>
    ) = Room.databaseBuilder(
        appContext,
        ExerciseSettingsDatabase::class.java,
        "exercise_settings_db"
    )
        .fallbackToDestructiveMigration()
        .addCallback(ExerciseSettingsCallback(provider))
        .build()

    @Singleton
    @Provides
    fun provideExerciseSettingsDao(db: ExerciseSettingsDatabase) = db.getExerciseSettingsDao()

    @Singleton
    @Provides
    fun provideSavedExerciseDao(db: ExerciseSettingsDatabase) = db.getSavedExerciseDao()
}

class ExerciseSettingsCallback(
    private val provider: Provider<ExerciseSettingsDao>
) : RoomDatabase.Callback() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch {
            val dao = provider.get()
            val defaults = defaultExerciseSettingsList()
            defaults.forEach {
                dao.insert(it.first, it.second)
            }
        }
    }
}