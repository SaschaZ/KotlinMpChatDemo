package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import kotlin.math.pow
import kotlin.random.Random

@Serializable
data class Color(val red: Int, val green: Int, val blue: Int) {
    constructor() : this(
        Random.nextInt(0, 255),
        Random.nextInt(0, 255),
        Random.nextInt(0, 255)
    )

    @OptIn(ExperimentalComposeWebWidgetsApi::class)
    val color: Color
        get() = Color(red, green, blue)

    val argb: String = "0x${red.hex}${green.hex}${blue.hex}"
}

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun Color.toArgb(): String = "0x${red.hex}${green.hex}${blue.hex}"

private val Int.hex: String
    get() {
        val power = (2.0.pow(4)).toInt()
        val lower = this % power
        val upper = this / power
        return "${upper.hexChar}${lower.hexChar}"
    }

private val Int.hexChar: String
    get() = when {
        this < 10 -> "$this"
        else -> when (this) {
            10 -> "A"
            11 -> "B"
            12 -> "C"
            13 -> "D"
            14 -> "E"
            else -> "F"
        }
    }

fun String.toColor(): dev.zieger.mpchatdemo.common.dto.Color {
    var idx = 0
    val (red, green, blue) = removePrefix("0x").groupBy { idx++ / 2 }.map { it.value }
    return dev.zieger.mpchatdemo.common.dto.Color(
        red.fromHexChar(),
        green.fromHexChar(),
        blue.fromHexChar()
    )
}

private fun List<Char>.fromHexChar(): Int {
    val power = (2.0.pow(4)).toInt()
    return this[1].fromHexChar() * power + this[0].fromHexChar()
}

private fun Char.fromHexChar(): Int =
    digitToIntOrNull() ?: when (this) {
        'A' -> 10
        'B' -> 11
        'C' -> 12
        'D' -> 13
        'E' -> 14
        else -> 15
    }