package com.github.dataanon.model

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class BlacklistTableUnitTest : FunSpec() {

    init {
        test("should return select query given no where clause") {
            val blacklistTable = BlacklistTable("MOVIE", listOf("MOVIE_ID"))
            blacklistTable.anonymize("TITLE")

            val selectQuery = blacklistTable.generateSelectQuery()

            selectQuery.trim() shouldBe "SELECT TITLE,MOVIE_ID FROM MOVIE"
        }

        test("should return select query given where clause") {
            val blacklistTable = BlacklistTable("MOVIE", listOf("MOVIE_ID"))
            blacklistTable.where("RELEASE_YEAR > 1989")
            blacklistTable.anonymize("TITLE")

            val selectQuery = blacklistTable.generateSelectQuery()

            selectQuery.trim() shouldBe "SELECT TITLE,MOVIE_ID FROM MOVIE  WHERE RELEASE_YEAR > 1989"
        }
    }
}