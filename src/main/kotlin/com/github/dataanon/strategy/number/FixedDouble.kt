package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy

class FixedDouble(private val value: Double) : AnonymizationStrategy<Double> {

    override fun anonymize(field: Field<Double>, record: Record): Double = value
}
