package com.github.dataanon.strategy.list

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomSampling

class PickFromList<T: Any>(private val values: List<T>) : AnonymizationStrategy<T>, RandomSampling {

    init {
        require(values.isNotEmpty(), {"values cannot be empty while using PickFromList"})
    }
    override fun anonymize(field: Field<T>, record: Record): T = sample(values)
}