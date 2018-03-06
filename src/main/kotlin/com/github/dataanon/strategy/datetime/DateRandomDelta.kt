package com.github.dataanon.strategy.datetime

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import java.time.LocalDate
import java.util.Random

class DateRandomDelta(val days: Int): AnonymizationStrategy<LocalDate> {
    override fun anonymize(field: Field<LocalDate>, record: Record): LocalDate {
        val randomInt = rand(-days,days)
        return when {
            randomInt > 0 -> field.oldValue.plusDays(randomInt.toLong())
            randomInt < 0 -> field.oldValue.minusDays(-randomInt.toLong())
            else -> field.oldValue
        }
    }

    private fun rand(start: Int, end: Int) = Random().nextInt(end + 1 - start) + start

}