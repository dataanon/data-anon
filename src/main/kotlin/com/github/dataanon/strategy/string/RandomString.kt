package com.github.dataanon.strategy.string

import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomUtils

open class RandomString(val charSet: String) : AnonymizationStrategy<String> {

    override fun anonymize(field: Field<String>, record: Record): String = RandomUtils.generateRandomString(length = field.oldValue.length, chars = charSet)
}