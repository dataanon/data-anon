package com.github.dataanon.integration

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import java.sql.Date
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpecialFeaturesIntegrationTest : FunSpec() {

    init {
        test("support to select specific records from table using where clause") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", Date(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable = MoviesTable(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        where("GENRE = 'Drama'")
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(progressBarEnabled = false)

            await().timeout(4, TimeUnit.SECONDS).until { destTable.findAll()[0]["TITLE"].toString().equals("MY VALUE") }
            val records = destTable.findAll()

            assertEquals(1, records.size)
            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])
            assertTrue(Pattern.compile("[a-zA-Z]+").matcher(records[0]["GENRE"].toString()).matches())

            sourceTable.close()
            destTable.close()
        }

        test("testing progress bar") {
            val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(progressBarEnabled = true)

            await().timeout(2, TimeUnit.SECONDS).until { moviesTable.findAll()[0]["TITLE"].toString().equals("MY VALUE") }
            val records = moviesTable.findAll()

            assertEquals(1, records.size)
            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])
            assertTrue(Pattern.compile("[a-zA-Z]+").matcher(records[0]["GENRE"].toString()).matches())

            moviesTable.close()
        }

        test("limit records for testing") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", Date(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable = MoviesTable(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        limit(1)
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(progressBarEnabled = false)

            await().timeout(2, TimeUnit.SECONDS).until { destTable.findAll()[0]["TITLE"].toString().equals("MY VALUE") }
            val records = destTable.findAll()

            assertEquals(1, records.size)
            val anonymizedRecord = records[0]
            assertEquals(1, anonymizedRecord["MOVIE_ID"])
            assertEquals("MY VALUE", anonymizedRecord["TITLE"])
            assertEquals(Date(1999, 5, 2), anonymizedRecord["RELEASE_DATE"])
            assertTrue(Pattern.compile("[a-zA-Z]+").matcher(anonymizedRecord["GENRE"].toString()).matches())

            sourceTable.close()
            destTable.close()
        }
    }
}