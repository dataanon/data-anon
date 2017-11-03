package com.github.dataanon.strategies

import com.github.dataanon.Field
import com.github.dataanon.Record

interface AnonymizationStrategy {
    fun anonymize(field: Field, record: Record): Any
}