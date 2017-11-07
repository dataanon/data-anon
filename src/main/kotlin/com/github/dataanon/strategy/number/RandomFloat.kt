package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

class RandomFloat(val from: Float = 0.0f, val to: Float = 100.0f) : AnonymizationStrategy<Float> {

    override fun anonymize(field: Field<Float>, record: Record): Float = RandomUtils.generateRandomFloat(from = from, to = to)
}