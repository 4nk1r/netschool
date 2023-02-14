package io.fournkoner.netschool.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.fournkoner.netschool.ui.components.SimpleToolbar
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors

class HelloWorldScreen : AndroidScreen() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                SimpleToolbar(
                    title = "TODO",
                    onBack = { navigator.pop() }
                )
            },
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hello World!")
                }
            },
            backgroundColor = LocalNetSchoolColors.current.backgroundMain
        )
    }
}