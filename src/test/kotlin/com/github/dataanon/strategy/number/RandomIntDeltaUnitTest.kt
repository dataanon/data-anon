package com.github.dataanon.strategy.number

import com.github.dataanon.Field
import com.github.dataanon.Matchers
import com.github.dataanon.Record
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec

class RandomIntDeltaUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should generate random int with default delta") {
            val randomIntDelta = RandomIntDelta()
            val field          = Field("id", 90)

            val anonymized = randomIntDelta.anonymize(field, emptyRecord)
            anonymized should beIn(80..100)
        }

        test("should generate random int with positive delta provided") {
            val randomIntDelta = RandomIntDelta(delta = 100)
            val field          = Field("id", 90)

            val anonymized = randomIntDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-10..190)
        }

        test("should generate random int with negative delta provided") {
            val randomIntDelta  = RandomIntDelta(delta = -50)
            val field           = Field("id", 20)

            val anonymized = randomIntDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-30..70)
        }
    }

}