package com.github.dataanon.model

import com.github.dataanon.db.jdbc.JdbcDbConfig
import com.github.dataanon.db.jdbc.JdbcTableReader
import com.github.dataanon.support.MoviesTable
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class BlacklistTableUnitTest : FunSpec() {

    init {
        test("should return select query given no where clause") {
            val blacklistTable = BlacklistTable("MOVIES", listOf("MOVIE_ID"))
            blacklistTable.anonymize("TITLE")

            val (dbConfig, _) = getEmptyMoviesTable()
            val reader = JdbcTableReader(dbConfig, blacklistTable)

            val selectQuery = reader.generateSelectQuery()

            selectQuery.trim() shouldBe "SELECT TITLE,MOVIE_ID FROM MOVIES"
        }

        test("should return select query given where clause") {
            val blacklistTable = BlacklistTable("MOVIES", listOf("MOVIE_ID"))
            blacklistTable.where("RELEASE_DATE > '1989-01-01'")
            blacklistTable.anonymize("TITLE")

            val (dbConfig, _) = getEmptyMoviesTable()
            val reader = JdbcTableReader(dbConfig, blacklistTable)
            val selectQuery = reader.generateSelectQuery()

            selectQuery.trim() shouldBe "SELECT TITLE,MOVIE_ID FROM MOVIES  WHERE RELEASE_DATE > '1989-01-01'"
        }
    }

    private fun getEmptyMoviesTable(): Pair<JdbcDbConfig, MoviesTable> {
        val dbConfig    = JdbcDbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)

        return Pair(dbConfig, moviesTable)
    }
}
