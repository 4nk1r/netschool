package io.fournkoner.netschool.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.navigation.tabs.*
import io.fournkoner.netschool.ui.screens.auth.AuthScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors

@Composable
fun AppNavigation() {
    Navigator(AuthScreen()) { navigator ->
        Android13NavigationTransition(navigator) {
            it.Content()
        }
    }
}

class AppScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        BottomSheetNavigator(
            sheetElevation = 0.dp,
            sheetBackgroundColor = Color.Transparent
        ) {
            TabNavigator(JournalTab) {
                Scaffold(
                    modifier = Modifier.fillMaxWidth(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = true,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LocalNetSchoolColors.current.backgroundMain)
                            ) {
                                Divider(color = LocalNetSchoolColors.current.divider)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(
                                            56.dp + WindowInsets.navigationBars
                                                .asPaddingValues()
                                                .calculateBottomPadding()
                                        )
                                        .animateContentSize()
                                ) {
                                    listOf(JournalTab, ReportsTab, MailTab, ScheduleTab).forEach {
                                        BottomNavigationItem(tab = it)
                                    }
                                }
                            }
                        }
                    },
                    content = { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = paddingValues.calculateBottomPadding())
                        ) {
                            CurrentTab()
                        }
                    },
                    backgroundColor = LocalNetSchoolColors.current.backgroundMain,
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .navigationBarsPadding()
            .selectable(
                selected = tabNavigator.current == tab,
                indication = rememberRipple(
                    bounded = false,
                    color = LocalNetSchoolColors.current.accentMain
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) { tabNavigator.current = tab },
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color by animateColorAsState(
            if (tabNavigator.current == tab) {
                LocalNetSchoolColors.current.accentMain
            } else {
                LocalNetSchoolColors.current.accentInactive
            }
        )

        Icon(
            painter = tab.options.icon!!,
            contentDescription = tab.options.title,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Text(
            text = tab.options.title.toUpperCase(Locale.current),
            fontSize = 8.sp,
            letterSpacing = 0.4.sp,
            fontFamily = FontFamily(Font(R.font.inter_semi_bold, FontWeight.SemiBold)),
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}