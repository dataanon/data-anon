package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

class RandomInt(val from: Int = 0, val to: Int = 100) : AnonymizationStrategy<Int> {

    override fun anonymize(field: Field<Int>, record: Record): Int = RandomUtils.generateRandomInt(from = from, to = to)
}