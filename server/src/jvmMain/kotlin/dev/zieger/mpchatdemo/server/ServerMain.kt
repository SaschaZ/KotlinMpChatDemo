package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.Constants
import dev.zieger.utils.time.TimeStamp
import kotlinx.coroutines.runBlocking
import java.time.ZoneId
import java.util.*


fun main(args: Array<String>) = runBlocking {
    org.apache.log4j.BasicConfigurator.configure()
    TimeStamp.DEFAULT_LOCALE = Locale.GERMANY
    TimeStamp.DEFAULT_TIME_ZONE = TimeZone.getTimeZone(ZoneId.systemDefault())

    val port = args.getOrNull(args.indexOf("-p") + 1)?.toIntOrNull() ?: Constants.INTERNAL_PORT
    val path = args.getOrNull(args.indexOf("--path") + 1) ?: Constants.PATH

    Server(Constants.INTERNAL_HOST, port, path).start()
}

