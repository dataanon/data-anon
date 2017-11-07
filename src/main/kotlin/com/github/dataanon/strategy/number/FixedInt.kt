package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy

class FixedInt(private val value: Int = 100) : AnonymizationStrategy<Int> {

    override fun anonymize(field: Field<Int>, record: Record): Int  = value
}