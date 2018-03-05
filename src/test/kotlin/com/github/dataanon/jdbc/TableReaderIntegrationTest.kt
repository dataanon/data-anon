package com.github.dataanon.jdbc

import com.github.dataanon.model.BlacklistTable
import com.github.dataanon.model.DbConfig
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.LocalDate

class TableReaderIntegrationTest : FunSpec() {

    init {
        test("should return the number of records specified by limit") {
            val (dbConfig, moviesTable) = prepareDataWith5Movies()
            val blacklistTable  = BlacklistTable("MOVIES", listOf("MOVIE_ID")).limit(2)

            val tableReader = TableReader(dbConfig, blacklistTable)

            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe false

            moviesTable.close()
        }

        test("should return all records given limit is -1") {
            val (dbConfig, moviesTable) = prepareDataWith5Movies()
            val blacklistTable  = BlacklistTable("MOVIES", listOf("MOVIE_ID"))

            val tableReader = TableReader(dbConfig, blacklistTable)

            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe true
            tableReader.hasNext() shouldBe true

            moviesTable.close()
        }
    }

    private fun prepareDataWith5Movies(): Pair<DbConfig, MoviesTable> {
        val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))
                .insert(3, "Movie 3", "Comedy", LocalDate.of(2005, 5, 2))
                .insert(4, "Movie 4", "Horror", LocalDate.of(2005, 5, 2))
                .insert(5, "Movie 5", "Fiction", LocalDate.of(2005, 5, 2))
        return Pair(dbConfig, moviesTable)
    }
}