package com.github.dataanon.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.strategies.DefaultStringStrategy
import com.github.dataanon.support.MoviesTable
import io.kotlintest.specs.StringSpec
import java.sql.Date
import kotlin.test.assertEquals


class MoviesSimplePrimaryKeyAcceptanceTest : StringSpec() {

    init {
        "should do blacklist anonmyzation for single record with simple primaryKey"{
            val dbConfig = hashMapOf("url" to "jdbc:h2:mem:movies", "user" to "", "password" to "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))


            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(DefaultStringStrategy("MY VALUE"))
                        anonymize("GENRE")
                    }.execute()

            val records = moviesTable.findAll()
            assertEquals(1,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals("DEFAULT VALUE", records[0]["GENRE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])

            moviesTable.close()
        }


        "should do blacklist anonmyzation for multiple records with simple primaryKey"{
            val dbConfig = hashMapOf("url" to "jdbc:h2:mem:movies", "user" to "", "password" to "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", Date(2005, 5, 2))


            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(DefaultStringStrategy("MY VALUE"))
                        anonymize("GENRE")
                    }.execute()

            val records = moviesTable.findAll()
            assertEquals(2,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals("DEFAULT VALUE", records[0]["GENRE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])

            assertEquals(2, records[1]["MOVIE_ID"])
            assertEquals("MY VALUE", records[1]["TITLE"])
            assertEquals("DEFAULT VALUE", records[1]["GENRE"])
            assertEquals(Date(2005, 5, 2), records[1]["RELEASE_DATE"])

            moviesTable.close()
        }


    }

}
