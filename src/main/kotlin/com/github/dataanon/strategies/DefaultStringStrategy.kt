package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultStringStrategy(private val value: String = "DEFAULT VALUE") : AnonymizationStrategy {
    override fun anonymize(field: Field, record: Record): Any {
        return value
    }
}