package io.fournkoner.netschool.ui.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.screens.journal.JournalScreen

object JournalTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.bn_journal)
            val icon = painterResource(R.drawable.ic_bn_journal)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(JournalScreen())
    }
}