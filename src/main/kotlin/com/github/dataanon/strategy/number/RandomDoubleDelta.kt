package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

class RandomDoubleDelta(private val delta: Double = 10.0) : AnonymizationStrategy<Double> {

    override fun anonymize(field: Field<Double>, record: Record): Double {

        val adjustment = if ( delta < 0 ) RandomUtils.generateRandomDouble(from = delta, to = -delta)
        else                              RandomUtils.generateRandomDouble(from = -delta,  to = delta)

        return field.oldValue + adjustment
    }
}