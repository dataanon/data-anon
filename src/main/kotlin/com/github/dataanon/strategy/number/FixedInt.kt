package com.github.dataanon.strategy.number

import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.strategy.AnonymizationStrategy

class FixedInt(val value: Int = 100) : AnonymizationStrategy<Int> {

    override fun anonymize(field: Field<Int>, record: Record): Int  = value
}