package com.github.dataanon.integration.whitelist

import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.match
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate

class WhitelistMoviesSimplePrimaryKeyIntegrationTest : FunSpec() {

    init {
        test("should do whitelist anonymization for multiple record with simple primaryKey"){
            val (sourceDbConfig, sourceTable) = prepareData()
            val destDbConfig      = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable         = MoviesTable(destDbConfig)

            assertEquals(0,destTable.findAll().size)

            Whitelist(sourceDbConfig,destDbConfig)
                    .table("MOVIES") {
                        whitelist("MOVIE_ID")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                        anonymize("RELEASE_DATE")
                    }.execute(progressBarEnabled = false)

            val records = destTable.findAll()

            records.size shouldBe 2
            records[0]["MOVIE_ID"] shouldBe 1
            records[0]["TITLE"] shouldBe "MY VALUE"
            records[0]["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            records[0]["GENRE"].toString() should match("[a-zA-Z]+")

            records[1]["MOVIE_ID"] shouldBe 2
            records[1]["TITLE"] shouldBe "MY VALUE"
            records[1]["RELEASE_DATE"] shouldBe LocalDate.of(2005, 5, 2)
            records[1]["GENRE"].toString() should match("[a-zA-Z]+")

            closeResources(sourceTable, destTable)
        }
    }

    private fun closeResources(sourceTable: MoviesTable, destTable: MoviesTable) {
        sourceTable.close()
        destTable.close()
    }

    private fun prepareData(): Pair<DbConfig, MoviesTable> {
        val sourceDbConfig = DbConfig("jdbc:h2:mem:movies_source", "", "")
        val sourceTable = MoviesTable(sourceDbConfig)
                .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))
        return Pair(sourceDbConfig, sourceTable)
    }
}