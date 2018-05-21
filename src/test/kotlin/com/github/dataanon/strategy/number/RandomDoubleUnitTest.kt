package com.github.dataanon.strategy.number

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.specs.FunSpec

class RandomDoubleUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should generate random double given with default range") {

            val field       = Field("price", 14454.45)
            val anonymized  = RandomDouble().anonymize(field, emptyRecord)

            anonymized should beIn(0.0..100.0)
        }

        test("should generate random double given a range") {

            val field       = Field("price", 0.0)
            val anonymized  = RandomDouble(from = 100.0, to = 1000.0).anonymize(field, emptyRecord)

            anonymized should beIn(100.0..1000.0)
        }

        test("should generate random double given a negative range") {

            val field       = Field("price", -9090.98)
            val anonymized  = RandomDouble(-1000.0, 0.0).anonymize(field, emptyRecord)

            anonymized should beIn(-1000.0..0.0)
        }
    }
}