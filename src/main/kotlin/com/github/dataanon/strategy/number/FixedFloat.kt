package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy

class FixedFloat(private val value: Float = 100.0f) : AnonymizationStrategy<Float> {

    override fun anonymize(field: Field<Float>, record: Record): Float = value
}
