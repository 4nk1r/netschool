package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.domain.repositories.AccountRepository
import io.fournkoner.netschool.domain.repositories.JournalRepository
import io.fournkoner.netschool.domain.usecases.account.GetAccountDataUseCase
import io.fournkoner.netschool.domain.usecases.account.LogoutUseCase
import io.fournkoner.netschool.domain.usecases.account.SignInUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetJournalUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UseCasesModule {

    ///////////////////////
    // ACCOUNT USE CASES //
    ///////////////////////

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

    @Singleton
    @Provides
    fun provideGetAccountDataUseCase(repository: AccountRepository): GetAccountDataUseCase {
        return GetAccountDataUseCase(repository)
    }

    ///////////////////////
    // JOURNAL USE CASES //
    ///////////////////////

    @Provides
    @Singleton
    fun provideGetJournalUseCase(repository: JournalRepository): GetJournalUseCase {
        return GetJournalUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetDetailedAssignmentsUseCase(repository: JournalRepository): GetDetailedAssignmentsUseCase {
        return GetDetailedAssignmentsUseCase(repository)
    }
}