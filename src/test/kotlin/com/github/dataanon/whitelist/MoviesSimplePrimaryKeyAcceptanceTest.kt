package com.github.dataanon.whitelist

import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.strategies.DefaultStringStrategy
import com.github.dataanon.support.MoviesTable
import io.kotlintest.specs.StringSpec
import java.sql.Date
import kotlin.test.assertEquals

class MoviesSimplePrimaryKeyAcceptanceTest : StringSpec() {

    init {
        "should do whitelist anonmyzation for multiple record with simple primaryKey"{
            val sourceDbConfig = hashMapOf("url" to "jdbc:h2:mem:movies_source", "user" to "", "password" to "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", Date(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", Date(2005, 5, 2))

            val destDbConfig = hashMapOf("url" to "jdbc:h2:mem:movies_dest", "user" to "", "password" to "")
            val destTable = MoviesTable(destDbConfig)
            assertEquals(0,destTable.findAll().size)


            Whitelist(sourceDbConfig,destDbConfig)
                    .table("MOVIES") {
                        whitelist("MOVIE_ID","RELEASE_DATE")
                        anonymize("TITLE").using(DefaultStringStrategy("MY VALUE"))
                        anonymize("GENRE")
                    }.execute()

            val records = destTable.findAll()
            assertEquals(2,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals("MY VALUE", records[0]["TITLE"])
            assertEquals("DEFAULT VALUE", records[0]["GENRE"])
            assertEquals(Date(1999, 5, 2), records[0]["RELEASE_DATE"])

            assertEquals(2, records[1]["MOVIE_ID"])
            assertEquals("MY VALUE", records[1]["TITLE"])
            assertEquals("DEFAULT VALUE", records[1]["GENRE"])
            assertEquals(Date(2005, 5, 2), records[1]["RELEASE_DATE"])

            sourceTable.close()
            destTable.close()
        }

    }

}