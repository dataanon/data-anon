package com.github.dataanon.integration.blacklist

import com.github.dataanon.Matchers
import com.github.dataanon.db.jdbc.JdbcDbConfig
import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.strategy.datetime.DateTimeRandomDelta
import com.github.dataanon.strategy.number.FixedInt
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import com.github.dataanon.support.RatingsTable
import io.kotlintest.matchers.match
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class BlacklistMultipleTableIntegrationTest : FunSpec(), Matchers {

    init {

        test("should do blacklist anonymization for multiple tables"){
            val (dbConfig, moviesTable, ratingsTable) = prepareData()

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }
                    .table("RATINGS", listOf("MOVIE_ID", "USER_ID")) {
                        anonymize("RATING").using(FixedInt(3))
                        anonymize("CREATED_AT").using(DateTimeRandomDelta(Duration.ofSeconds(250)))
                    }
                    .execute(progressBarEnabled = false)

            val moviesRecords = moviesTable.findAll()
            val ratingRecords = ratingsTable.findAll()


            moviesRecords.size shouldBe 1
            moviesRecords[0]["MOVIE_ID"] shouldBe 1
            moviesRecords[0]["TITLE"] shouldBe "MY VALUE"
            moviesRecords[0]["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            moviesRecords[0]["GENRE"].toString() should match("[a-zA-Z]+")

            ratingRecords.size shouldBe 2
            ratingRecords[0]["MOVIE_ID"] shouldBe 1
            ratingRecords[0]["USER_ID"] shouldBe 1
            ratingRecords[0]["RATING"] shouldBe 3
            val timestamp1 = LocalDateTime.ofEpochSecond(1509701304, 0, ZoneOffset.UTC)
            (ratingRecords[0]["CREATED_AT"] as LocalDateTime) should beIn(timestamp1.minusSeconds(300)..timestamp1.plusSeconds(300))

            ratingRecords[1]["MOVIE_ID"] shouldBe 1
            ratingRecords[1]["USER_ID"] shouldBe 2
            ratingRecords[1]["RATING"] shouldBe 3
            val timestamp2 = LocalDateTime.ofEpochSecond(1509701310, 0, ZoneOffset.UTC)
            (ratingRecords[0]["CREATED_AT"] as LocalDateTime) should beIn(timestamp2.minusSeconds(300)..timestamp2.plusSeconds(300))

            closeResources(moviesTable, ratingsTable)
        }
    }

    private fun closeResources(moviesTable: MoviesTable, ratingsTable: RatingsTable) {
        moviesTable.close()
        ratingsTable.close()
    }

    private fun prepareData(): Triple<JdbcDbConfig, MoviesTable, RatingsTable> {
        val dbConfig = JdbcDbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig).insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
        val ratingsTable = RatingsTable(dbConfig)
                            .insert(1, 1, 4, LocalDateTime.ofEpochSecond(1509701304,0, ZoneOffset.UTC))
                            .insert(1, 2, 5, LocalDateTime.ofEpochSecond(1509701310,0, ZoneOffset.UTC))
        return Triple(dbConfig, moviesTable, ratingsTable)
    }
}
