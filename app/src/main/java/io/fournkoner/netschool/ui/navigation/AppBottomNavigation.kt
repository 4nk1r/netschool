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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.screens.MailScreen
import io.fournkoner.netschool.ui.screens.ReportsScreen
import io.fournkoner.netschool.ui.screens.ScheduleScreen
import io.fournkoner.netschool.ui.screens.auth.AuthScreen
import io.fournkoner.netschool.ui.screens.journal.JournalScreen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun AppBottomNavigation() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            AnimatedVisibility(
                visible = currentDestination?.route != Screen.Auth.route && currentDestination != null,
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
                        BottomNavigationItem(
                            icon = painterResource(R.drawable.ic_bn_journal),
                            screen = Screen.Journal,
                            navController = navController
                        )
                        BottomNavigationItem(
                            icon = painterResource(R.drawable.ic_bn_reports),
                            screen = Screen.Reports,
                            navController = navController
                        )
                        BottomNavigationItem(
                            icon = painterResource(R.drawable.ic_bn_mail),
                            screen = Screen.Mail,
                            navController = navController
                        )
                        BottomNavigationItem(
                            icon = painterResource(R.drawable.ic_bn_schedule),
                            screen = Screen.Schedule,
                            navController = navController
                        )
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
                AppNavHost(navController)
            }
        },
        backgroundColor = LocalNetSchoolColors.current.backgroundMain
    )
}

@Composable
private fun RowScope.BottomNavigationItem(
    icon: Painter,
    screen: Screen,
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isSelected by remember(currentDestination) {
        mutableStateOf(currentDestination?.hierarchy?.any { it.route == screen.route } == true)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .navigationBarsPadding()
            .selectable(
                selected = isSelected,
                indication = rememberRipple(
                    bounded = false,
                    color = LocalNetSchoolColors.current.accentMain
                ),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color by animateColorAsState(
            if (isSelected) {
                LocalNetSchoolColors.current.accentMain
            } else {
                LocalNetSchoolColors.current.accentInactive
            }
        )

        Icon(
            painter = icon,
            contentDescription = stringResource(screen.resourceId!!),
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Text(
            text = stringResource(screen.resourceId).toUpperCase(Locale.current),
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AppNavHost(navController: NavHostController) {
    val slideDistance = rememberSlideDistance()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        enterTransition = { materialSharedAxisXIn(true, slideDistance) },
        popEnterTransition = { materialSharedAxisXIn(false, slideDistance) },
        exitTransition = { materialSharedAxisXOut(true, slideDistance) },
        popExitTransition = { materialSharedAxisXOut(false, slideDistance) },
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Auth.route) { AuthScreen(navController) }
        composable(Screen.Journal.route) { JournalScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.Mail.route) { MailScreen(navController) }
        composable(Screen.Schedule.route) { ScheduleScreen() }
    }
}