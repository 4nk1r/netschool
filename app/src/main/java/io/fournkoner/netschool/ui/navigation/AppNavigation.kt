package io.fournkoner.netschool.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
import io.fournkoner.netschool.ui.components.BadgedLayout
import io.fournkoner.netschool.ui.navigation.tabs.JournalTab
import io.fournkoner.netschool.ui.navigation.tabs.MailTab
import io.fournkoner.netschool.ui.navigation.tabs.ReportsTab
import io.fournkoner.netschool.ui.screens.auth.AuthScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.utils.Const
import io.fournkoner.netschool.utils.formattedShortString

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
            sheetBackgroundColor = Color.Transparent,
            scrimColor = Color.Black.copy(alpha = 0.4f),
            skipHalfExpanded = true
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
                                    listOf(JournalTab, ReportsTab, MailTab).forEach {
                                        BottomNavigationItem(
                                            tab = it,
                                            unreadCount = if (it == MailTab) Const.mailUnreadMessages else 0
                                        )
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
                    backgroundColor = LocalNetSchoolColors.current.backgroundMain
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    tab: Tab,
    unreadCount: Int = 0
) {
    val tabNavigator = LocalTabNavigator.current

    BadgedLayout(
        badge = {
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                        .background(LocalNetSchoolColors.current.badge, CircleShape)
                        .padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = unreadCount.formattedShortString,
                        color = LocalNetSchoolColors.current.onBadge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
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
            ) { tabNavigator.current = tab }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
}
