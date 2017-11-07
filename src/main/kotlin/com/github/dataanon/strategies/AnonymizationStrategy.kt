package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

interface AnonymizationStrategy<T: Any> {
    fun anonymize(field: Field<T>, record: Record): T
}