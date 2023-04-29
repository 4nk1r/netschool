package io.fournkoner.netschool.ui.navigation

import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import io.fournkoner.netschool.ui.screens.journal.info.AssignmentInfoViewModel
import io.fournkoner.netschool.ui.screens.mail.message.MailMessageViewModel

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModelsModule {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(AssignmentInfoViewModel.Factory::class)
    abstract fun provideAssignmentInfoViewModelFactory(
        factory: AssignmentInfoViewModel.Factory
    ): ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(MailMessageViewModel.Factory::class)
    abstract fun provideMailMessageViewModelFactory(
        factory: MailMessageViewModel.Factory
    ): ScreenModelFactory
}
