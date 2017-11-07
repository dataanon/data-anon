package com.github.dataanon.strategy.number

import com.github.dataanon.model.Field
import com.github.dataanon.Matchers
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.specs.FunSpec

class RandomFloatUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should generate random float given with default range") {

            val field       = Field("price", 14454.45f)
            val anonymized  = RandomFloat().anonymize(field, emptyRecord)

            anonymized should beIn(0.0f..100.0f)
        }

        test("should generate random float given a range") {

            val field       = Field("price", 0.0f)
            val anonymized  = RandomFloat(from = 100.0f, to = 1000.0f).anonymize(field, emptyRecord)

            anonymized should beIn(100.0f..1000.0f)
        }

        test("should generate random float given a negative range") {

            val field       = Field("price", -9090.98f)
            val anonymized  = RandomFloat(-1000.0f, 0.0f).anonymize(field, emptyRecord)

            anonymized should beIn(-1000.0f..0.0f)
        }
    }
}