package io.fournkoner.netschool.ui.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.navigation.Android13NavigationTransition
import io.fournkoner.netschool.ui.screens.reports.ReportsScreen

object ReportsTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.bn_reports)
            val icon = painterResource(R.drawable.ic_bn_reports)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ReportsScreen()) { navigator ->
            Android13NavigationTransition(navigator) {
                it.Content()
            }
        }
    }
}
