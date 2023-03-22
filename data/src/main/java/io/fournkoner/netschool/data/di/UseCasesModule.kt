package io.fournkoner.netschool.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.fournkoner.netschool.domain.repositories.AccountRepository
import io.fournkoner.netschool.domain.repositories.JournalRepository
import io.fournkoner.netschool.domain.repositories.MailRepository
import io.fournkoner.netschool.domain.repositories.ReportsRepository
import io.fournkoner.netschool.domain.usecases.account.GetAccountDataUseCase
import io.fournkoner.netschool.domain.usecases.account.LogoutUseCase
import io.fournkoner.netschool.domain.usecases.account.SignInUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetDetailedAssignmentsUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetHeadersForDownloaderUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetJournalUseCase
import io.fournkoner.netschool.domain.usecases.journal.GetMailMessageDetailedUseCase
import io.fournkoner.netschool.domain.usecases.mail.*
import io.fournkoner.netschool.domain.usecases.reports.*
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

    @Provides
    @Singleton
    fun provideGetCookiesForDownloaderUseCase(repository: JournalRepository): GetHeadersForDownloaderUseCase {
        return GetHeadersForDownloaderUseCase(repository)
    }

    ///////////////////////
    // REPORTS USE CASES //
    ///////////////////////

    @Provides
    @Singleton
    fun provideGetShortReportRequestDataUseCase(repository: ReportsRepository): GetShortReportRequestDataUseCase {
        return GetShortReportRequestDataUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateShortReportUseCase(repository: ReportsRepository): GenerateShortReportUseCase {
        return GenerateShortReportUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSubjectReportRequestDataUseCase(repository: ReportsRepository): GetSubjectReportRequestDataUseCase {
        return GetSubjectReportRequestDataUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateSubjectReportUseCase(repository: ReportsRepository): GenerateSubjectReportUseCase {
        return GenerateSubjectReportUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateFinalReportUseCase(repository: ReportsRepository): GenerateFinalReportUseCase {
        return GenerateFinalReportUseCase(repository)
    }

    ////////////////////
    // MAIL USE CASES //
    ////////////////////

    @Provides
    @Singleton
    fun provideGetUnreadMessagesCountUseCase(repository: MailRepository): GetUnreadMessagesCountUseCase {
        return GetUnreadMessagesCountUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMailboxUseCase(repository: MailRepository): GetMailboxUseCase {
        return GetMailboxUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMailMessageDetailedUseCase(repository: MailRepository): GetMailMessageDetailedUseCase {
        return GetMailMessageDetailedUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteMessagesUseCase(repository: MailRepository): DeleteMessagesUseCase {
        return DeleteMessagesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMessageReceiversUseCase(repository: MailRepository): GetMessageReceiversUseCase {
        return GetMessageReceiversUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMessageFileSizeLimitUseCase(repository: MailRepository): GetMessageFileSizeLimitUseCase {
        return GetMessageFileSizeLimitUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSendMessageUseCase(repository: MailRepository): SendMessageUseCase {
        return SendMessageUseCase(repository)
    }
}