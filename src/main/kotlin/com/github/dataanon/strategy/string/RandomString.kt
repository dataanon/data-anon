package com.github.dataanon.strategy.string

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

open class RandomString(val charSet: String) : AnonymizationStrategy<String> {

    override fun anonymize(field: Field<String>, record: Record): String = RandomUtils.generateRandomString(length = field.oldValue.length, chars = charSet)
}