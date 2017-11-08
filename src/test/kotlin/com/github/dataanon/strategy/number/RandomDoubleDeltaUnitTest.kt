package com.github.dataanon.strategy.number

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec

class RandomDoubleDeltaUnitTest : FunSpec(), Matchers{

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should generate random double with default delta") {
            val randomFloatDelta = RandomDoubleDelta()
            val field            = Field("price", 90.0)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(80.0..100.0)
        }

        test("should generate random double with positive delta provided") {
            val randomFloatDelta = RandomDoubleDelta(delta = 100.0)
            val field            = Field("price", 90.0)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-10.0..190.0)
        }

        test("should generate random double with negative delta provided") {
            val randomFloatDelta = RandomDoubleDelta(delta = -50.0)
            val field            = Field("price", 20.0)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-30.0..70.0)
        }
    }
}