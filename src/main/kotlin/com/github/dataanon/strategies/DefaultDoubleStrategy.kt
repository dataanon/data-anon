package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultDoubleStrategy(private val value: Double = 4.2) : AnonymizationStrategy {
    override fun anonymize(field: Field, record: Record): Any {
        return value
    }
}