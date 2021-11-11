package dev.zieger.mpchatdemo.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi

@ExperimentalComposeWebWidgetsApi
@Composable
expect fun Link(url: String, content: String)