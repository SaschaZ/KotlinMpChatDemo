package dev.zieger.mpchatdemo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.zieger.mpchatdemo.common.Chat
import kotlinx.coroutines.InternalCoroutinesApi

class MainActivity : ComponentActivity() {
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Chat("zieger.dev/chat", 80)
        }
    }
}