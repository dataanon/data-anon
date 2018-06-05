package com.github.dataanon.strategy.name

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File

class RandomFirstNameUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return random First name from default file") {
            val firstNames      = File(this::class.java.getResource("/data/first_names.dat").path).readLines()
            val randomFirstName = RandomFirstName()
            val field           = Field("first_name", "John")

            val anonymized = randomFirstName.anonymize(field, emptyRecord)
            anonymized should beIn(firstNames)
        }

        test("should return random First name from file provided") {
            val firstNames      = File(this::class.java.getResource("/test_first_names.txt").path).readLines()
            val randomFirstName = RandomFirstName(sourceFilePath = "/test_first_names.txt")
            val field           = Field("first_name", "John")

            val anonymized = randomFirstName.anonymize(field, emptyRecord)
            anonymized should beIn(firstNames)
        }

        test("should throw IllegalArgumentException given file path is blank"){
            shouldThrow<IllegalArgumentException> { RandomFirstName(sourceFilePath = "") }
        }
    }
}