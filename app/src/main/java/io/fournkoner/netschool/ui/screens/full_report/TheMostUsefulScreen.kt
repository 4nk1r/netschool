package io.fournkoner.netschool.ui.screens.full_report

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Typography

class TheMostUsefulScreen : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                SimpleToolbar(
                    title = stringResource(R.string.reports_full_name),
                    onBack = navigator::pop
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_coming_soon),
                        contentDescription = stringResource(R.string.full_report_coming_soon),
                        tint = LocalNetSchoolColors.current.accentMain
                    )
                    Text(
                        text = stringResource(R.string.full_report_coming_soon),
                        style = Typography.h6.copy(color = LocalNetSchoolColors.current.textMain),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.full_report_coming_soon_desc),
                        style = Typography.body1.copy(color = LocalNetSchoolColors.current.textSecondary),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }
}