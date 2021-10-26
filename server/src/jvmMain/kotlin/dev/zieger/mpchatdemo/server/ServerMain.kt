package dev.zieger.mpchatdemo.server

import dev.zieger.mpchatdemo.common.Constants
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>) = runBlocking {
    org.apache.log4j.BasicConfigurator.configure()

    val port = args.getOrNull(args.indexOf("-p") + 1)?.toIntOrNull() ?: Constants.PORT
    val path = args.getOrNull(args.indexOf("--path") + 1) ?: Constants.PATH

    Server(Constants.INTERNAL_HOST, port, path).start()
}

