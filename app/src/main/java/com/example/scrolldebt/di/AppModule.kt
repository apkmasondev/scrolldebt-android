package com.example.scrolldebt.di

import android.content.Context
import androidx.room.Room
import com.example.scrolldebt.data.db.ScrollDebtDatabase
import com.example.scrolldebt.data.db.UsageDao
import com.example.scrolldebt.data.repository.PreferencesManager
import com.example.scrolldebt.domain.usecases.BrutalTruthEngine
import com.example.scrolldebt.utils.UsageStatsHelper
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
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideScrollDebtDatabase(@ApplicationContext context: Context): ScrollDebtDatabase {
        return ScrollDebtDatabase.getDatabase(context)
    }

    @Provides
    fun provideUsageDao(database: ScrollDebtDatabase): UsageDao {
        return database.usageDao()
    }

    @Provides
    @Singleton
    fun provideUsageStatsHelper(@ApplicationContext context: Context): UsageStatsHelper {
        return UsageStatsHelper(context)
    }

    @Provides
    @Singleton
    fun provideBrutalTruthEngine(@ApplicationContext context: Context): BrutalTruthEngine {
        return BrutalTruthEngine(context)
    }
}
