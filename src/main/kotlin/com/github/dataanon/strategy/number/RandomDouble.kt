package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

class RandomDouble(private val from: Double = 0.0, private val to: Double = 100.0) : AnonymizationStrategy<Double> {

    override fun anonymize(field: Field<Double>, record: Record): Double = RandomUtils.generateRandomDouble(from = from, to = to)
}