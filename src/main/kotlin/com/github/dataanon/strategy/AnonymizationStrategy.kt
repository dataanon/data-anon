package com.github.dataanon.strategy

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record

interface AnonymizationStrategy<T: Any> {
    fun anonymize(field: Field<T>, record: Record): T
}