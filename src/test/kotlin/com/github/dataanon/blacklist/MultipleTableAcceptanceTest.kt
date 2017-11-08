package com.github.dataanon.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.number.FixedInt
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import com.github.dataanon.support.RatingsTable
import io.kotlintest.specs.StringSpec
import java.sql.Date
import java.sql.Timestamp
import kotlin.test.assertEquals

class MultipleTableAcceptanceTest : StringSpec() {

    init {
        "should do blacklist anonmyzation for multiple tables"{

            val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
            val ratingsTable = RatingsTable(dbConfig)
                    .insert(1, 1, 4, Timestamp(1509701304))
                    .insert(1, 2, 5, Timestamp(1509701310))

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
            assertEquals(1, moviesRecords.size)
            assertEquals(1, moviesRecords[0]["MOVIE_ID"])
            assertEquals("MY VALUE", moviesRecords[0]["TITLE"])
            assertEquals("DEFAULT VALUE", moviesRecords[0]["GENRE"])
            assertEquals(Date(1999, 5, 2), moviesRecords[0]["RELEASE_DATE"])
            moviesTable.close()

            val ratingRecords = ratingsTable.findAll()
            assertEquals(2,ratingRecords.size)
            assertEquals(1, ratingRecords[0]["MOVIE_ID"])
            assertEquals(1, ratingRecords[0]["USER_ID"])
            assertEquals(3, ratingRecords[0]["RATING"])
            assertEquals(Timestamp(1509701304), ratingRecords[0]["CREATED_AT"])
            assertEquals(1, ratingRecords[1]["MOVIE_ID"])
            assertEquals(2, ratingRecords[1]["USER_ID"])
            assertEquals(3, ratingRecords[1]["RATING"])
            assertEquals(Timestamp(1509701310), ratingRecords[1]["CREATED_AT"])
            ratingsTable.close()

        }
    }

}