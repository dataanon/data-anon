package com.github.dataanon.strategy.string

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File

class StringTemplateUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a string after evaluating row number") {
            val stringTemplate = StringTemplate(template = "user_#{row_number}")
            val field          = Field("user_name", "John")

            val  anonymized = stringTemplate.anonymize(field, Record(listOf(), 100))

            anonymized shouldBe "user_100"
        }

        test("should return a string after evaluating row number with field value") {
            val stringTemplate = StringTemplate(template = "#{field_value}_#{row_number}")
            val field          = Field("user_name", "John")

            val  anonymized = stringTemplate.anonymize(field, Record(listOf(), 100))

            anonymized shouldBe "John_100"
        }

        test("should return a string with random full name consisting of first name and last name separated by underscore") {
            val stringTemplate = StringTemplate(template = "#{random_fn}_#{random_ln}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            val nameComponents  = anonymized.split("_")

            nameComponents.size shouldBe 2
        }

        test("should return a string with random first name and last name") {
            val firstNames      = File(this::class.java.getResource("/data/first_names.dat").path).readLines()
            val lastNames       = File(this::class.java.getResource("/data/last_names.dat").path).readLines()

            val stringTemplate = StringTemplate(template = "#{random_fn}_#{random_ln}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            val nameComponents  = anonymized.split("_")

            nameComponents[0] should beIn(firstNames)
            nameComponents[1] should beIn(lastNames)
        }

        test("should return a string with random email") {
            val stringTemplate = StringTemplate(template = "#{random_email}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            anonymized should contains("data-anonymization.com")
        }

        test("should return a string with an email including field value") {
            val stringTemplate = StringTemplate(template = "#{field_value}@thoughtworks.com")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            anonymized shouldBe "John@thoughtworks.com"
        }

        test("should return a random alphabetic string with length based on field value") {
            val stringTemplate = StringTemplate(template = "#{random_alpha}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            anonymized.length  shouldBe 4
        }

        test("should return a random alphabetic string") {
            val stringTemplate = StringTemplate(template = "#{random_alpha}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            anonymized         should notMatches(Regex.fromLiteral("[0-9]+"))
        }

        test("should return a random alphanumeric string") {
            val stringTemplate = StringTemplate(template = "#{random_alpha_numeric}")
            val field          = Field("user_name", "John")

            val anonymized      = stringTemplate.anonymize(field, emptyRecord)
            anonymized.length  shouldBe 4
        }

        test("should throw IllegalArgumentException given template is blank"){
            shouldThrow<IllegalArgumentException> { StringTemplate(template = "") }
        }
    }
}