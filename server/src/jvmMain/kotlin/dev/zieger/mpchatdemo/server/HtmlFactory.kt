package dev.zieger.mpchatdemo.server

import kotlinx.css.*
import kotlinx.html.*

fun HTML.rootHtml() {
    head {
        title("KotlinMpChatDemo")
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    }
    body {
        // This div tag will be used by jetpack compose to render our content.
        div { id = "root" }

        // Generated JavaScript file of the web module. It is important, that this definition is
        // called after the root div tag or jetpack compose will fail to start.
        script(src = "/static/web.js") {}
    }
}

fun CSSBuilder.rootCss() {
    body {
        backgroundColor = Color.lightGray
        margin(5.px)
    }
    rule("root") {
        width = 100.vw - 10.px
        display = Display.flex
    }
}