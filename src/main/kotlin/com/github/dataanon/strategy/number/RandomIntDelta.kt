package com.github.dataanon.strategy.number

import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

class RandomIntDelta(val delta: Int = 10) : AnonymizationStrategy<Int> {

    override fun anonymize(field: Field<Int>, record: Record): Int {

        val adjustment = if ( delta < 0 ) RandomUtils.generateRandomInt(from = delta,  to = -delta)
                         else             RandomUtils.generateRandomInt(from = -delta, to = delta)

        return field.oldValue + adjustment
    }
}