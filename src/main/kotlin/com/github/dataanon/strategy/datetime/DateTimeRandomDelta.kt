package com.github.dataanon.strategy.datetime

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import java.time.Duration
import java.time.LocalDateTime
import java.util.Random

class DateTimeRandomDelta(private val duration: Duration) : AnonymizationStrategy<LocalDateTime> {

    override fun anonymize(field: Field<LocalDateTime>, record: Record): LocalDateTime {
        val durationInSeconds = duration.seconds
        val randomSeconds = rand(durationInSeconds)

        return when (Random().nextBoolean()) {
            true -> field.oldValue.plusSeconds(randomSeconds)
            false -> field.oldValue.minusSeconds(randomSeconds)
        }
    }

    private fun rand(range: Long): Long = (Random().nextDouble() * range).toLong()

}
