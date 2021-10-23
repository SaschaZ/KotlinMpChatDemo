package dev.zieger.mpchatdemo.common.dto

import kotlinx.serialization.Serializable
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import kotlin.math.pow

@Serializable
data class Color(val red: Int, val green: Int, val blue: Int) {

    @OptIn(ExperimentalComposeWebWidgetsApi::class)
    val color: Color
        get() = Color(red, green, blue)
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

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun String.toColor(): Color {
    var idx = 0
    val (red, green, blue) = removePrefix("0x").groupBy { idx++ / 2 }.map { it.value }
    return Color(red.fromHexChar(), green.fromHexChar(), blue.fromHexChar())
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