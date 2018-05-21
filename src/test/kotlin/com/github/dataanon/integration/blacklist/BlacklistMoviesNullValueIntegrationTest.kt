package com.github.dataanon.integration.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.strategy.string.RandomAlphabetic
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.match
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.LocalDate

class BlacklistMoviesNullValueIntegrationTest : FunSpec() {

    init {
        test("should do blacklist anonymization for multiple records with movie genre as null and anonymization strategy is provided for null field") {

            val (dbConfig, moviesTable) = prepareDataWith2Movies()

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(RandomAlphabetic())
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()

            records.size shouldBe 2
            records[0]["MOVIE_ID"] shouldBe 1
            records[0]["TITLE"] shouldBe "MY VALUE"
            records[0]["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            records[0]["GENRE"] shouldBe null

            records[1]["MOVIE_ID"] shouldBe 2
            records[1]["TITLE"] shouldBe "MY VALUE"
            records[1]["RELEASE_DATE"] shouldBe LocalDate.of(2005, 5, 2)
            records[1]["GENRE"].toString() should match("[a-zA-Z]+")

            moviesTable.close()
        }

        test("should do blacklist anonymization for multiple records with movie genre as null") {

            val (dbConfig, moviesTable) = prepareDataWith2Movies()

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()

            records.size shouldBe 2
            records[0]["MOVIE_ID"] shouldBe 1
            records[0]["TITLE"] shouldBe "MY VALUE"
            records[0]["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            records[0]["GENRE"] shouldBe null

            records[1]["MOVIE_ID"] shouldBe 2
            records[1]["TITLE"] shouldBe "MY VALUE"
            records[1]["RELEASE_DATE"] shouldBe LocalDate.of(2005, 5, 2)
            records[1]["GENRE"].toString() should match("[a-zA-Z]+")

            moviesTable.close()
        }
    }

    private fun prepareDataWith2Movies(): Pair<DbConfig, MoviesTable> {
        val dbConfig    = DbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", null, LocalDate.of(1999, 5, 2))
                .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))

        return Pair(dbConfig, moviesTable)
    }
}