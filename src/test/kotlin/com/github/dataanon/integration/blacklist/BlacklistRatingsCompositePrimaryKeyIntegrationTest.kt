package com.github.dataanon.integration.blacklist

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.number.FixedInt
import com.github.dataanon.support.RatingsTable
import io.kotlintest.specs.FunSpec
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

class BlacklistRatingsCompositePrimaryKeyIntegrationTest : FunSpec() {

    init {
        test("should do blacklist anonymization for multiple record with composite primaryKey"){
            val (dbConfig, ratingsTable) = prepareData()

            Blacklist(dbConfig)
                    .table("RATINGS",listOf("MOVIE_ID", "USER_ID")) {
                        anonymize("RATING").using(FixedInt(3))
                    }.execute(progressBarEnabled = false)

            val records = ratingsTable.findAll()

            assertEquals(2,records.size)
            assertEquals(1, records[0]["MOVIE_ID"])
            assertEquals(1, records[0]["USER_ID"])
            assertEquals(3, records[0]["RATING"])
            assertEquals(LocalDateTime.ofEpochSecond(1509701304,0, ZoneOffset.UTC), records[0]["CREATED_AT"])

            assertEquals(1, records[1]["MOVIE_ID"])
            assertEquals(2, records[1]["USER_ID"])
            assertEquals(3, records[1]["RATING"])
            assertEquals(LocalDateTime.ofEpochSecond(1509701310,0, ZoneOffset.UTC), records[1]["CREATED_AT"])

            ratingsTable.close()
        }
    }

    private fun prepareData(): Pair<DbConfig, RatingsTable> {
        val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
        val ratingsTable = RatingsTable(dbConfig)
                .insert(1, 1, 4, LocalDateTime.ofEpochSecond(1509701304,0, ZoneOffset.UTC))
                .insert(1, 2, 5, LocalDateTime.ofEpochSecond(1509701310,0, ZoneOffset.UTC))
        return Pair(dbConfig, ratingsTable)
    }
}