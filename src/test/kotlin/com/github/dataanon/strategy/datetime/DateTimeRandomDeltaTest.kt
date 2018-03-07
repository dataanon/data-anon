package com.github.dataanon.strategy.datetime

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.FunSpec
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateTimeRandomDeltaTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should anonymize date within given duration in form of duration") {
            val myTable = table(
                headers("durationInSeconds"),
                row(1000L),
                row(23439L),
                row(1000000L),
                row(1238192734L)
            )
            forAll(myTable) { durationInSeconds ->
                val timestamp = LocalDateTime.ofEpochSecond(1509701304, 0, ZoneOffset.UTC)
                val field = Field("LAST_UPDATED_BY", timestamp)
                val anonymized = DateTimeRandomDelta(Duration.ofSeconds(durationInSeconds)).anonymize(field, emptyRecord)
                anonymized should beIn(timestamp.minusSeconds(durationInSeconds)..timestamp.plusSeconds(durationInSeconds))

            }
        }

    }
}