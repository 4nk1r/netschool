package io.fournkoner.netschool.data.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.data.network.AuthService
import io.fournkoner.netschool.data.network.JournalService
import io.fournkoner.netschool.data.network.ReportsService
import io.fournkoner.netschool.data.repositories.AccountRepositoryImpl
import io.fournkoner.netschool.data.repositories.JournalRepositoryImpl
import io.fournkoner.netschool.data.repositories.ReportsRepositoryImpl
import io.fournkoner.netschool.domain.repositories.AccountRepository
import io.fournkoner.netschool.domain.repositories.JournalRepository
import io.fournkoner.netschool.domain.repositories.ReportsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoriesModule {

    @Provides
    @Singleton
    fun provideAccountRepository(
        authService: AuthService,
        @EncryptedPreferences encryptedPreferences: SharedPreferences
    ): AccountRepository {
        return AccountRepositoryImpl(authService, encryptedPreferences)
    }

    @Provides
    @Singleton
    fun provideJournalRepository(journalService: JournalService): JournalRepository {
        return JournalRepositoryImpl(journalService)
    }

    @Singleton
    @Provides
    fun provideReportsRepository(reportsService: ReportsService): ReportsRepository {
        return ReportsRepositoryImpl(reportsService)
    }
}