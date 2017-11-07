package com.github.dataanon.strategy.name

import com.github.dataanon.model.Field
import com.github.dataanon.Matchers
import com.github.dataanon.model.Record
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec
import java.io.File

class RandomFullNameUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return random full name from default sources") {
            val firstNames      = File(this::class.java.getResource("/data/first_names.dat").path).readLines()
            val lastNames       = File(this::class.java.getResource("/data/last_names.dat").path).readLines()
            val field           = Field("name", "John Smith")
            val randomFullName  = RandomFullName()

            val anonymized     = randomFullName.anonymize(field, emptyRecord)
            val nameComponents = anonymized.split(" ")

            nameComponents[0] should beIn(firstNames)
            nameComponents[1] should beIn(lastNames)
        }

        test("should return random full name consisting of first name and last name separated by space from default sources") {
            val field           = Field("name", "John Smith")
            val randomFullName  = RandomFullName()

            val anonymized     = randomFullName.anonymize(field, emptyRecord)
            val nameComponents = anonymized.split(" ")

            nameComponents.size shouldEqual 2
        }

        test("should return random full name from provided sources") {
            val firstNames      = File(this::class.java.getResource("/test_first_names.txt").path).readLines()
            val lastNames       = File(this::class.java.getResource("/test_last_names.txt").path).readLines()
            val field           = Field("name", "John Smith")
            val randomFullName  = RandomFullName(firstNameSourceFilePath = this::class.java.getResource("/test_first_names.txt").path,
                                                 lastNameSourceFilePath  = this::class.java.getResource("/test_last_names.txt").path
                                                 )

            val anonymized     = randomFullName.anonymize(field, emptyRecord)
            val nameComponents = anonymized.split(" ")

            nameComponents[0] should beIn(firstNames)
            nameComponents[1] should beIn(lastNames)
        }

        test("should return random full name  consisting of first name and last name separated by space from provided sources") {
            val field           = Field("name", "John Smith")
            val randomFullName  = RandomFullName(firstNameSourceFilePath = this::class.java.getResource("/test_first_names.txt").path,
                    lastNameSourceFilePath  = this::class.java.getResource("/test_last_names.txt").path
            )

            val anonymized     = randomFullName.anonymize(field, emptyRecord)
            val nameComponents = anonymized.split(" ")

            nameComponents.size shouldEqual 2
        }
    }
}