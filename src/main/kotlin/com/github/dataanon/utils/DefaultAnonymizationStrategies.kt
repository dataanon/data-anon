package com.github.dataanon.utils

import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.CopyAnonymizationStrategy
import com.github.dataanon.strategy.bool.RandomBooleanTrueFalse
import com.github.dataanon.strategy.number.RandomDouble
import com.github.dataanon.strategy.number.RandomFloat
import com.github.dataanon.strategy.number.RandomInt
import com.github.dataanon.strategy.string.RandomAlphabetic
import kotlin.reflect.KClass

object DefaultAnonymizationStrategies {

    private val defaultStrategies = mutableMapOf<KClass<*>, AnonymizationStrategy<*>>()

    init {
        defaultStrategies.put(String::class,    RandomAlphabetic())
        defaultStrategies.put(Boolean::class,   RandomBooleanTrueFalse())
        defaultStrategies.put(Int::class,       RandomInt())
        defaultStrategies.put(Float::class,     RandomFloat())
        defaultStrategies.put(Double::class,    RandomDouble())
    }

    fun getAnonymizationStrategy(kClass: KClass<*>) = if (defaultStrategies[kClass] != null) defaultStrategies[kClass]
                                                      else CopyAnonymizationStrategy<Any>()
}