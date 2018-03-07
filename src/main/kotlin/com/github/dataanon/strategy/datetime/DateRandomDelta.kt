package com.github.dataanon.strategy.datetime

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import java.time.LocalDate
import java.util.Random

class DateRandomDelta(private val days: Int): AnonymizationStrategy<LocalDate> {

    override fun anonymize(field: Field<LocalDate>, record: Record): LocalDate {
        val randomInt = Random().nextInt(days).toLong()
        return when (Random().nextBoolean()) {
            true -> field.oldValue.plusDays(randomInt)
            false -> field.oldValue.minusDays(randomInt)
        }
    }

}