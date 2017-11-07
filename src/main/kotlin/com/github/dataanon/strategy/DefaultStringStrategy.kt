package com.github.dataanon.strategy

import com.github.dataanon.Field
import com.github.dataanon.Record

class DefaultStringStrategy(private val value: String = "DEFAULT VALUE") : AnonymizationStrategy<String> {
    override fun anonymize(field: Field<String>, record: Record): String {
        return value
    }
}