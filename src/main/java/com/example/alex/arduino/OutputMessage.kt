package com.example.alex.arduino

import java.io.OutputStream

interface OutputMessage

data class botonA(val unit: Unit) : OutputMessage
data class botonB(val unit: Unit) : OutputMessage
data class botonC(val unit: Unit) : OutputMessage
data class botonD(val unit: Unit) : OutputMessage
data class botonE(val unit: Unit) : OutputMessage
data class botonF(val unit: Unit) : OutputMessage
data class botonG(val unit: Unit) : OutputMessage

val B = botonB(Unit)
val C = botonC(Unit)
val D = botonD(Unit)
val E = botonE(Unit)
val F = botonF(Unit)
val G = botonG(Unit)
val A = botonA(Unit)

fun OutputStream.writeMessage(message: OutputMessage) = write(

        when (message) {
            is botonA -> 65
            is botonF -> 70
            is botonG -> 71
            is botonB -> 66
            is botonC -> 67
            is botonD -> 68
            is botonE -> 69
            else -> 65
        }
)


private fun speedToByte(speed: Float) = (Math.abs(speed) * 255f).toByte()
