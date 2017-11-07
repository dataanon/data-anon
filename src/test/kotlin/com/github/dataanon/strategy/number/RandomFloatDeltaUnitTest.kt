package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.Matchers
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec

class RandomFloatDeltaUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should generate random float with default delta") {
            val randomFloatDelta = RandomFloatDelta()
            val field            = Field("price", 90.0f)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(80.0f..100.0f)
        }

        test("should generate random float with positive delta provided") {
            val randomFloatDelta = RandomFloatDelta(delta = 100.0f)
            val field            = Field("price", 90.0f)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-10.0f..190.0f)
        }

        test("should generate random float with negative delta provided") {
            val randomFloatDelta = RandomFloatDelta(delta = -50.0f)
            val field            = Field("price", 20.0f)

            val anonymized = randomFloatDelta.anonymize(field, emptyRecord)
            anonymized should beIn(-30.0f..70.0f)
        }
    }

}