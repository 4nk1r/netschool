package io.fournkoner.netschool.ui.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import io.fournkoner.netschool.R

// one day, but not this time
object ScheduleTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.bn_schedule)
            val icon = painterResource(R.drawable.ic_bn_schedule)

            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        TODO()
    }
}
