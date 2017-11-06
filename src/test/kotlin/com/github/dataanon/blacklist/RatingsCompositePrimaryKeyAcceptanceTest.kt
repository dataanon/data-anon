package com.github.dataanon.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.strategies.DefaultIntStrategy
import com.github.dataanon.support.RatingsTable
import io.kotlintest.specs.StringSpec
import java.sql.Timestamp
import kotlin.test.assertEquals

class RatingsCompositePrimaryKeyAcceptanceTest : StringSpec() {

    init {
        "should do blacklist anonmyzation for multiple record with composite primaryKey"{
            val dbConfig = hashMapOf("url" to "jdbc:h2:mem:movies", "user" to "", "password" to "")
            val ratingsTable = RatingsTable(dbConfig)
                    .insert(1, 1, 4, Timestamp(1509701304))
                    .insert(1, 2, 5, Timestamp(1509701310))


            Blacklist(dbConfig)
                    .table("RATINGS",listOf("MOVIE_ID", "USER_ID")) {
                        anonymize("RATING").using(DefaultIntStrategy(3))
                    }.execute()

            val records = ratingsTable.findAll()
            assertEquals(2,records.size)

            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals(1, records[0]["USER_ID"])
            assertEquals(3, records[0]["RATING"])
            assertEquals(Timestamp(1509701304), records[0]["CREATED_AT"])

            assertEquals(1, records[1]["MOVIE_ID"])
            assertEquals(2, records[1]["USER_ID"])
            assertEquals(3, records[1]["RATING"])
            assertEquals(Timestamp(1509701310), records[1]["CREATED_AT"])

            ratingsTable.close()
        }




    }

}