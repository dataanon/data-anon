package com.github.dataanon.strategy.email

import com.github.dataanon.model.Field
import com.github.dataanon.Matchers
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec

class RandomEmailUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a random email with default host"){
            val randomEmail = RandomEmail()
            val field       = Field("email", "sample@gmail.com")

            val anonymized  = randomEmail.anonymize(field, emptyRecord)
            anonymized should contains("data-anonymization.com")
        }

        test("should return a random email with host and tld provided"){
            val randomEmail = RandomEmail(host = "thoughtworks", tld = "org")
            val field       = Field("email", "sample@gmail.com")

            val anonymized  = randomEmail.anonymize(field, emptyRecord)
            anonymized should contains("thoughtworks.org")
        }

        test("should throw IllegalArgumentException given host is blank"){
            shouldThrow<IllegalArgumentException> { RandomEmail(host = "") }
        }

        test("should throw IllegalArgumentException given tld is blank"){
            shouldThrow<IllegalArgumentException> { RandomEmail(host = "thoughtworks", tld = "") }
        }
    }
}