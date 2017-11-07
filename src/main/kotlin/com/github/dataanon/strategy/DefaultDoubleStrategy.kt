package com.github.dataanon.strategy

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultDoubleStrategy(private val value: Double = 4.2) : AnonymizationStrategy<Double> {
    override fun anonymize(field: Field<Double>, record: Record): Double {
        return value
    }
}