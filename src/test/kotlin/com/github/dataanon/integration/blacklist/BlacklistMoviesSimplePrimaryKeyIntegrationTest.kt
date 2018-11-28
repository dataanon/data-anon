package com.github.dataanon.integration.blacklist

import com.github.dataanon.Matchers
import com.github.dataanon.db.jdbc.JdbcDbConfig
import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.strategy.datetime.DateRandomDelta
import com.github.dataanon.strategy.list.PickFromDatabase
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.match
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.LocalDate

class BlacklistMoviesSimplePrimaryKeyIntegrationTest : FunSpec(), Matchers {

    init {
        test("should do blacklist anonymization for single record with simple primaryKey"){

            val (dbConfig, moviesTable) = prepareDataWithSingleMovie()

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(PickFromDatabase<String>(dbConfig,"SELECT DISTINCT GENRE FROM MOVIES"))
                        anonymize("RELEASE_DATE").using(DateRandomDelta(10))
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()

            records.size shouldBe 1
            records[0]["MOVIE_ID"] shouldBe 1
            records[0]["TITLE"] shouldBe "MY VALUE"
            (records[0]["RELEASE_DATE"] as LocalDate) should beIn(LocalDate.of(1999, 5, 2).minusDays(10)..LocalDate.of(1999, 5, 2).plusDays(10))
            records[0]["GENRE"].toString() should beIn(listOf("Drama"))

            moviesTable.close()
        }

        test("should do blacklist anonymization for multiple records with simple primaryKey"){
            val (dbConfig, moviesTable) = prepareDataWith2Movies()

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                        anonymize("RELEASE_DATE").using(DateRandomDelta(5))
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()

            records.size shouldBe 2
            records[0]["MOVIE_ID"] shouldBe 1
            records[0]["TITLE"] shouldBe "MY VALUE"
            (records[0]["RELEASE_DATE"] as LocalDate) should beIn(LocalDate.of(1999, 5, 2).minusDays(5)..LocalDate.of(1999, 5, 2).plusDays(5))
            records[0]["GENRE"].toString() should match("[a-zA-Z]+")

            records[1]["MOVIE_ID"] shouldBe 2
            records[1]["TITLE"] shouldBe "MY VALUE"
            (records[1]["RELEASE_DATE"] as LocalDate) should beIn(LocalDate.of(2005, 5, 2).minusDays(5)..LocalDate.of(2005, 5, 2).plusDays(5))
            records[1]["GENRE"].toString() should match("[a-zA-Z]+")

            moviesTable.close()
        }
    }

    private fun prepareDataWith2Movies(): Pair<JdbcDbConfig, MoviesTable> {
        val dbConfig = JdbcDbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))
        return Pair(dbConfig, moviesTable)
    }

    private fun prepareDataWithSingleMovie(): Pair<JdbcDbConfig, MoviesTable> {
        val dbConfig = JdbcDbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig)
                .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
        return Pair(dbConfig, moviesTable)
    }
}
