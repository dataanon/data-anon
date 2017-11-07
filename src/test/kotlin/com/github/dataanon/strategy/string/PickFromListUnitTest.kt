package com.github.dataanon.strategy.string

import com.github.dataanon.model.Field
import com.github.dataanon.Matchers
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.PickFromList
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec

class PickFromListUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a random value from list") {
            val countries = listOf("India", "US", "UK")
            val field     = Field("country", "India")
            val strategy  = PickFromList(countries)

            val anonymized = strategy.anonymize(field, emptyRecord)

            anonymized should beIn(countries)
        }

        test("should throw IllegalArgumentException given empty list") {
            val countries = emptyList<String>()
            shouldThrow<IllegalArgumentException> {
                PickFromList(countries)
            }
        }
    }
}