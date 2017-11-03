package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultIntStrategy(private val value: Int = 100) : AnonymizationStrategy {
    override fun anonymize(field: Field, record: Record): Any {
        return value
    }
}