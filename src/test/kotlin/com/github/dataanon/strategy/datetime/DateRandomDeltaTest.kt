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
import java.time.LocalDate

class DateRandomDeltaTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should anonymize date within given duration in days") {
            val myTable = table(
                headers("days"),
                row(10),
                row(100)
            )
            forAll(myTable) { days ->
                val dateOfBirth = LocalDate.of(1999, 4, 4)
                val field = Field("DATE_OF_BIRTH", dateOfBirth)
                val anonymized = DateRandomDelta(days).anonymize(field, emptyRecord)
                anonymized should beIn(dateOfBirth.minusDays(days.toLong())..dateOfBirth.plusDays(days.toLong()))

            }
        }

    }
}