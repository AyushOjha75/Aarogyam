package com.aarogyam.di

import android.content.Context
import com.aarogyam.data.datastore.PreferencesRepository
import com.aarogyam.data.db.AppDatabase
import com.aarogyam.data.db.WeightLogDao
import com.aarogyam.data.repository.WeightRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWeightLogDao(database: AppDatabase): WeightLogDao {
        return database.weightLogDao()
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideWeightRepository(
        dao: WeightLogDao,
        preferencesRepository: PreferencesRepository
    ): WeightRepository {
        // Use the internal constructor via reflection-safe approach:
        // WeightRepository.getInstance deduplicates; but for Hilt we need a direct provide.
        // We expose a secondary factory method here.
        return WeightRepository.create(dao, preferencesRepository)
    }
}
