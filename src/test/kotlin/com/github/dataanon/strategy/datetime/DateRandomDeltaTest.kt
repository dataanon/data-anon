package com.github.dataanon.strategy.datetime

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.specs.FunSpec
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import java.time.LocalDate

class DateRandomDeltaTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should anonymize date within given duration in days") {
            val myTable = table(
                headers("days"),
                row(10),
                row(53),
                row(100),
                row(3450)
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