package com.github.dataanon.strategy.bool

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.specs.FunSpec

class RandomBooleanUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return either true or false") {
            val randomBoolean = RandomBooleanTrueFalse()
            val field         = Field("access_allowed", false)

            val anonymized = randomBoolean.anonymize(field, emptyRecord)
            anonymized should beIn(listOf(true, false))
        }

        test("should return either 1 or 0") {
            val randomBoolean = RandomBooleanOneZero()
            val field         = Field("access_allowed", 0)

            val anonymized = randomBoolean.anonymize(field, emptyRecord)
            anonymized should beIn(listOf(1, 0))
        }

        test("should return either Y or N") {
            val randomBoolean = RandomBooleanYN()
            val field         = Field("access_allowed", "N")

            val anonymized = randomBoolean.anonymize(field, emptyRecord)
            anonymized should beIn(listOf("Y", "N"))
        }
    }
}