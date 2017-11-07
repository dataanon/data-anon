package com.github.dataanon.strategy

import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.utils.RandomSampling

class PickFromList<T: Any>(val values: List<T>) : AnonymizationStrategy<T>, RandomSampling {

    init {
        require(values.isNotEmpty(), {"values cannot be empty while using PickFromList"})
    }
    override fun anonymize(field: Field<T>, record: Record): T = sample(values)
}