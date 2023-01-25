package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.domain.repositories.AccountRepository
import io.fournkoner.netschool.domain.usecases.account.LogoutUseCase
import io.fournkoner.netschool.domain.usecases.account.SignInUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UseCasesModule {

    @Singleton
    @Provides
    fun provideSignInUseCase(repository: AccountRepository): SignInUseCase {
        return SignInUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideLogOutUseCase(repository: AccountRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }
}