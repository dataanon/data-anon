package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultIntStrategy(private val value: Int = 100) : AnonymizationStrategy<Int> {
    override fun anonymize(field: Field<Int>, record: Record): Int {
        return value
    }
}