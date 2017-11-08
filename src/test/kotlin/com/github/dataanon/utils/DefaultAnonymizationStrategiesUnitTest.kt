package com.github.dataanon.utils

import com.github.dataanon.exception.NoAnonymizationStrategyAvailableException
import com.github.dataanon.strategy.number.RandomDouble
import com.github.dataanon.strategy.number.RandomFloat
import com.github.dataanon.strategy.number.RandomInt
import com.github.dataanon.strategy.string.RandomAlphabetic
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec
import java.util.*

class DefaultAnonymizationStrategiesUnitTest : FunSpec(){

    init {
        test("should return RandomAlphabetic for String as default AnonymizationStrategy"){
            val kClassString = String::class
            val anonymizationStrategy = DefaultAnonymizationStrategies.getAnonymizationStrategy(kClassString)

            anonymizationStrategy.should { beInstanceOf(RandomAlphabetic::class) }
        }

        test("should return RandomInt for Int as default AnonymizationStrategy"){
            val kClassInt = Int::class
            val anonymizationStrategy = DefaultAnonymizationStrategies.getAnonymizationStrategy(kClassInt)

            anonymizationStrategy.should { beInstanceOf(RandomInt::class) }
        }

        test("should return RandomFloat for Float as default AnonymizationStrategy"){
            val kClassFloat = Float::class
            val anonymizationStrategy = DefaultAnonymizationStrategies.getAnonymizationStrategy(kClassFloat)

            anonymizationStrategy.should { beInstanceOf(RandomFloat::class) }
        }

        test("should return RandomDouble for Double as default AnonymizationStrategy"){
            val kClassDouble = Double::class
            val anonymizationStrategy = DefaultAnonymizationStrategies.getAnonymizationStrategy(kClassDouble)

            anonymizationStrategy.should { beInstanceOf(RandomDouble::class) }
        }

        test("no throw NoAnonymizationStrategyAvailableException given unsupported data type"){
            val kClassDate = Date::class

            shouldThrow<NoAnonymizationStrategyAvailableException> {
                DefaultAnonymizationStrategies.getAnonymizationStrategy(kClassDate)
            }
        }
    }
}