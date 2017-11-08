package com.github.dataanon.strategy

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record

class CopyAnonymizationStrategy<T: Any> : AnonymizationStrategy<T>{

    override fun anonymize(field: Field<T>, record: Record): T = field.oldValue
}