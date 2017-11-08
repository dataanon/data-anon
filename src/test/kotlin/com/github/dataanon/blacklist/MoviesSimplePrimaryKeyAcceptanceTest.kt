package com.github.dataanon.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import java.sql.Date
import java.util.regex.Pattern
import kotlin.test.assertEquals


class MoviesSimplePrimaryKeyAcceptanceTest : StringSpec() {

    init {
        "should do blacklist anonmyzation for single record with simple primaryKey"{

            val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
            val alphabeticPattern = Pattern.compile("[a-zA-Z]+")

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()
            assertEquals(1,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])

            val matcher = alphabeticPattern.matcher(records[0]["GENRE"].toString())
            matcher.matches() shouldEqual true

            moviesTable.close()
        }

        "should do blacklist anonmyzation for multiple records with simple primaryKey"{
            val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", Date(2005, 5, 2))
            val alphabeticPattern = Pattern.compile("[a-zA-Z]+")

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = false)

            val records = moviesTable.findAll()
            assertEquals(2,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])

            val firstRecordGenreMatcher = alphabeticPattern.matcher(records[0]["GENRE"].toString())
            firstRecordGenreMatcher.matches() shouldEqual true

            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])

            assertEquals(2, records[1]["MOVIE_ID"])
            assertEquals("MY VALUE", records[1]["TITLE"])

            val secondRecordGenreMatcher    = alphabeticPattern.matcher(records[0]["GENRE"].toString())
            secondRecordGenreMatcher.matches() shouldEqual true

            assertEquals(Date(2005, 5, 2), records[1]["RELEASE_DATE"])

            moviesTable.close()
        }
    }
}