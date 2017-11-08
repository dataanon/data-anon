package com.github.dataanon.utils

import com.github.dataanon.utils.RandomCharacterSets.LOWERCASE
import java.util.*
object RandomUtils {

    fun generateRandomString(length:Int = 10, chars:String): String {
        val random      = Random()
        val randomBound = chars.length

        return StringBuilder().apply {
            for (index in 1..length) {
                append(chars[random.nextInt(randomBound)])
            }
        }
                .toString()
    }

    fun generateRandomFloat(from: Float = 0.0f, to:Float = 100.0f): Float {
        require(to >= from)
        return Random().nextFloat() * (to - from) + from
    }

    fun generateRandomDouble(from: Double = 0.0, to:Double = 100.0): Double {
        require(to >= from)
        return Random().nextDouble() * (to - from) + from
    }

    fun generateRandomInt(from: Int = 0, to:Int = 100): Int {
        require(to >= from)
        return Random().nextInt((to - from) + 1) + from
    }

    fun generateRandomCharacter(chars: String = LOWERCASE): Char = chars[Random().nextInt(chars.length)]
}

object RandomCharacterSets {
    const val ALPHANUMERIC = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ0123456789"
    const val LOWERCASE    = "abcdefghjkmnpqrstuvwxyz"
    const val UPPERCASE    = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    const val ALPHA        = "$LOWERCASE$UPPERCASE"
}