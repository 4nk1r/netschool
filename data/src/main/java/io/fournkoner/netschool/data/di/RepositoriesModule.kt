package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.data.network.AuthService
import io.fournkoner.netschool.data.repositories.AccountRepositoryImpl
import io.fournkoner.netschool.domain.repositories.AccountRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoriesModule {

    @Singleton
    @Provides
    fun provideAccountRepository(authService: AuthService): AccountRepository {
        return AccountRepositoryImpl(authService)
    }
}