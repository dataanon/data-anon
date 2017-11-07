package com.github.dataanon.strategy.string

import com.github.dataanon.Field
import com.github.dataanon.Matchers
import com.github.dataanon.Record
import com.github.dataanon.strategy.PickFromFile
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File

class PickFromFileUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should pick a random value from a file containing string") {
            val filePath      = this::class.java.getResource("/test_countries.txt").path
            val values        = File(filePath).readLines()
            val field         = Field("country", "India")
            val pickFromFile  = PickFromFile<String>(filePath = filePath)

            val anonymized = pickFromFile.anonymize(field, emptyRecord)
            anonymized should beIn(values)
        }

        test("should pick a random value from a file containing string using PickStringFromFile") {
            val filePath      = this::class.java.getResource("/test_countries.txt").path
            val values        = File(filePath).readLines()
            val field         = Field("country", "India")
            val pickFromFile  = PickStringFromFile(filePath = filePath)

            val anonymized = pickFromFile.anonymize(field, emptyRecord)
            anonymized should beIn(values)
        }

        test("should pick a random value from a file containing double") {
            val filePath      = this::class.java.getResource("/test_stock-prices.txt").path
            val values        = File(filePath).readLines().map { it.toDouble() }
            val field         = Field("stock-price", 12.90)
            val pickFromFile  = PickFromFile<Double>(filePath = filePath)

            val anonymized = pickFromFile.anonymize(field, emptyRecord)
            anonymized should beIn(values)
        }

        test("should throw IllegalArgumentException given file path is blank"){
            shouldThrow<IllegalArgumentException> { PickFromFile<Double>(filePath = "") }
        }
    }
}