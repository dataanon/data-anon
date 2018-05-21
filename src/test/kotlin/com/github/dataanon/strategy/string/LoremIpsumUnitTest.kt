package com.github.dataanon.strategy.string

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class LoremIpsumUnitTest : FunSpec() {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return LoremIpsum string with length based on the value of field") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "Parker")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized.length shouldBe 6
        }

        test("should return LoremIpsum string based on the value of field") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "Parker")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized        shouldBe "Lorem "
        }

        test("should return LoremIpsum string with length based on the value of field given length of field is greater than LoremIpsum string") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "Hello $DEFAULT_TEXT")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized.length shouldBe DEFAULT_TEXT_LENGTH
        }

        test("should return LoremIpsum string based on the value of field given length of field is greater than LoremIpsum string") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "Hello $DEFAULT_TEXT")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized        shouldBe DEFAULT_TEXT.trimIndent()
        }

        test("should return LoremIpsum string with length based on the value of field given length of field is equal to LoremIpsum string") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", DEFAULT_TEXT)

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized.length shouldBe DEFAULT_TEXT_LENGTH
        }

        test("should return LoremIpsum string based on the value of field given length of field is equal to LoremIpsum string") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", DEFAULT_TEXT)

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized        shouldBe DEFAULT_TEXT.trimIndent()
        }

        test("should return blank string with length given field value is blank") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized.length shouldBe 0
        }

        test("should return blank string given field value is blank") {

            val loremIpsum = LoremIpsum()
            val field      = Field("first_name", "")

            val anonymized = loremIpsum.anonymize(field, emptyRecord)

            anonymized        shouldBe ""
        }
    }
}