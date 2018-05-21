package com.github.dataanon.model

import com.github.dataanon.strategy.AnonymizationStrategy
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class WhitelistTableUnitTest : FunSpec() {

    init {
        test("should return insert query mentioning both whitelisted and anonymized columns") {
            val whitelistTable = WhitelistTable("MOVIE")

            whitelistTable.whitelist("GENRE")
            whitelistTable.anonymize("TITLE").using(object: AnonymizationStrategy<String>{
                override fun anonymize(field: Field<String>, record: Record): String = "new movie title"
            })

            val writeQuery = whitelistTable.generateWriteQuery()

            writeQuery.trim() shouldBe "INSERT INTO MOVIE(GENRE, TITLE) VALUES(?,?)"
        }

        test("should return insert query mentioning only whitelisted columns") {
            val whitelistTable = WhitelistTable("MOVIE")

            whitelistTable.whitelist("GENRE")

            val writeQuery = whitelistTable.generateWriteQuery()

            writeQuery.trim() shouldBe "INSERT INTO MOVIE(GENRE) VALUES(?)"
        }

        test("should return insert query mentioning only anonymized columns") {
            val whitelistTable = WhitelistTable("MOVIE")

            whitelistTable.anonymize("TITLE").using(object: AnonymizationStrategy<String>{
                override fun anonymize(field: Field<String>, record: Record): String = "new movie title"
            })

            val writeQuery = whitelistTable.generateWriteQuery()

            writeQuery.trim() shouldBe "INSERT INTO MOVIE(TITLE) VALUES(?)"
        }
    }
}