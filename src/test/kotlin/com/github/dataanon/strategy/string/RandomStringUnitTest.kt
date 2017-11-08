package com.github.dataanon.strategy.string

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class RandomStringUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return random string based specified character set") {

            val field       = Field("first_name", "John")
            val anonymized  = RandomString(charSet = "abc").anonymize(field, emptyRecord)

            anonymized.length shouldBe 4
        }

        test("should return random string based on field value") {

            val field       = Field("first_name", "John")
            val anonymized  = RandomAlphaNumeric().anonymize(field, emptyRecord)

            anonymized.length shouldBe 4
        }

        test("should return random string based on field value much greater than seed string") {

            val value       = "Some long field value which needs to be tested for generating random string"
            val field       = Field("first_name", value)
            val anonymized  = RandomAlphaNumeric().anonymize(field, emptyRecord)

            anonymized.length shouldBe value.length
        }

        test("should return blank string given field value is blank") {

            val field       = Field("first_name", "")
            val anonymized  = RandomAlphaNumeric().anonymize(field, emptyRecord)

            anonymized.length shouldBe 0
        }

        test("should return a random alphabetic string with length based on the field value") {

            val strategy = RandomAlphabetic()
            val field    = Field("first_name", "John")

            val anonymized = strategy.anonymize(field, emptyRecord)
            anonymized.length shouldEqual 4
        }

        test("should return a random alphabetic string") {

            val strategy = RandomAlphabetic()
            val field    = Field("first_name", "John")

            val anonymized = strategy.anonymize(field, emptyRecord)
            anonymized should notMatches(Regex.fromLiteral("[0-9]+"))
        }
    }
}