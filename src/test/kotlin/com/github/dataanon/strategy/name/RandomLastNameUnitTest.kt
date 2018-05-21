package com.github.dataanon.strategy.name

import com.github.dataanon.Matchers
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import io.kotlintest.should
import io.kotlintest.specs.FunSpec
import java.io.File

class RandomLastNameUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return random Last name from default file") {
            val firstNames      = File(this::class.java.getResource("/data/last_names.dat").path).readLines()
            val randomLastName  = RandomLastName()
            val field           = Field("last_name", "John")

            val anonymized = randomLastName.anonymize(field, emptyRecord)
            anonymized should beIn(firstNames)
        }

        test("should return random Last name from file provided") {
            val firstNames      = File(this::class.java.getResource("/test_last_names.txt").path).readLines()
            val randomLastName  = RandomLastName(sourceFilePath = this::class.java.getResource("/test_last_names.txt").path)
            val field           = Field("last_name", "John")

            val anonymized = randomLastName.anonymize(field, emptyRecord)
            anonymized should beIn(firstNames)
        }
    }
}