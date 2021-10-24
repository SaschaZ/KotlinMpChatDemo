package dev.zieger.mpchatdemo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.zieger.mpchatdemo.common.chat.Chat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.apache.log4j.BasicConfigurator.configure()

        setContent {
            Chat()
        }
    }
}