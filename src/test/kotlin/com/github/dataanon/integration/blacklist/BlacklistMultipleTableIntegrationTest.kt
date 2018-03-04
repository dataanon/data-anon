package com.github.dataanon.integration.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.number.FixedInt
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import com.github.dataanon.support.RatingsTable
import io.kotlintest.specs.FunSpec
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlacklistMultipleTableIntegrationTest : FunSpec() {

    init {

        test("should do blacklist anonymization for multiple tables"){
            val (dbConfig, moviesTable, ratingsTable) = prepareData()
            val pattern                               = Pattern.compile("[a-zA-Z]+")

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }
                    .table("RATINGS", listOf("MOVIE_ID", "USER_ID")) {
                        anonymize("RATING").using(FixedInt(3))
                    }
                    .execute(progressBarEnabled = false)

            val moviesRecords = moviesTable.findAll()
            val ratingRecords = ratingsTable.findAll()

            assertEquals(1, moviesRecords.size)
            assertEquals(1, moviesRecords[0]["MOVIE_ID"])
            assertEquals("MY VALUE", moviesRecords[0]["TITLE"])
            assertEquals(LocalDate.of(1999, 5, 2), moviesRecords[0]["RELEASE_DATE"])
            assertTrue(pattern.matcher(moviesRecords[0]["GENRE"].toString()).matches())

            assertEquals(2,ratingRecords.size)
            assertEquals(1, ratingRecords[0]["MOVIE_ID"])
            assertEquals(1, ratingRecords[0]["USER_ID"])
            assertEquals(3, ratingRecords[0]["RATING"])
            assertEquals(LocalDateTime.ofEpochSecond(1509701304,0, ZoneOffset.UTC), ratingRecords[0]["CREATED_AT"])
            assertEquals(1, ratingRecords[1]["MOVIE_ID"])
            assertEquals(2, ratingRecords[1]["USER_ID"])
            assertEquals(3, ratingRecords[1]["RATING"])
            assertEquals(LocalDateTime.ofEpochSecond(1509701310,0, ZoneOffset.UTC), ratingRecords[1]["CREATED_AT"])

            closeResources(moviesTable, ratingsTable)
        }
    }

    private fun closeResources(moviesTable: MoviesTable, ratingsTable: RatingsTable) {
        moviesTable.close()
        ratingsTable.close()
    }

    private fun prepareData(): Triple<DbConfig, MoviesTable, RatingsTable> {
        val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
        val moviesTable = MoviesTable(dbConfig).insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
        val ratingsTable = RatingsTable(dbConfig)
                            .insert(1, 1, 4, LocalDateTime.ofEpochSecond(1509701304,0, ZoneOffset.UTC))
                            .insert(1, 2, 5, LocalDateTime.ofEpochSecond(1509701310,0, ZoneOffset.UTC))
        return Triple(dbConfig, moviesTable, ratingsTable)
    }
}