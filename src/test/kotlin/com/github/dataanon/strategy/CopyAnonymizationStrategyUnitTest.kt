package com.github.dataanon.strategy

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class CopyAnonymizationStrategyUnitTest : FunSpec() {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return String value as is given CopyAnonymizationStrategy") {

            val field                      = Field("name", "John")
            val copyAnonymizationStrategy  = CopyAnonymizationStrategy<String>()
            val anonymized                 = copyAnonymizationStrategy.anonymize(field, emptyRecord)

            anonymized shouldBe "John"
        }

        test("should return Double value as is given CopyAnonymizationStrategy") {

            val field                      = Field("tax", 12.56)
            val copyAnonymizationStrategy  = CopyAnonymizationStrategy<Double>()
            val anonymized                 = copyAnonymizationStrategy.anonymize(field, emptyRecord)

            anonymized shouldBe 12.56
        }

        test("should return Int value as is given CopyAnonymizationStrategy") {

            val field                      = Field("tax", 12)
            val copyAnonymizationStrategy  = CopyAnonymizationStrategy<Int>()
            val anonymized                 = copyAnonymizationStrategy.anonymize(field, emptyRecord)

            anonymized shouldBe 12
        }
    }
}