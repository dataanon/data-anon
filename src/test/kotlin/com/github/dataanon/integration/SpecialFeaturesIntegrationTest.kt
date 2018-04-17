package com.github.dataanon.integration

import com.github.dataanon.dsl.Blacklist
import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.model.DbConfig
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.support.MoviesTable
import com.github.dataanon.support.MoviesTableHavingGenreSize10
import com.github.dataanon.utils.DataAnonTestLogHandler
import io.kotlintest.matchers.*
import io.kotlintest.specs.FunSpec
import java.time.LocalDate
import java.util.logging.Level

class SpecialFeaturesIntegrationTest : FunSpec() {

    init {
        test("support to select specific records from table using where clause") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable = MoviesTable(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        where("GENRE = 'Drama'")
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(false)

            val records = destTable.findAll()

            records.size shouldBe 1

            val anonymizedRecord = records[0]
            anonymizedRecord["MOVIE_ID"] shouldBe 1
            anonymizedRecord["TITLE"] shouldBe "MY VALUE"
            anonymizedRecord["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            anonymizedRecord["GENRE"].toString() should match("[a-zA-Z]+")

            sourceTable.close()
            destTable.close()
        }

        test("testing progress bar") {
            val dbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val moviesTable = MoviesTable(dbConfig)
                    .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))

            Blacklist(dbConfig)
                    .table("MOVIES", listOf("MOVIE_ID")) {
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE")
                    }.execute(true)

            val records = moviesTable.findAll()

            records.size shouldBe 1

            val anonymizedRecord = records[0]
            anonymizedRecord["MOVIE_ID"] shouldBe 1
            anonymizedRecord["TITLE"] shouldBe "MY VALUE"
            anonymizedRecord["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            anonymizedRecord["GENRE"].toString() should match("[a-zA-Z]+")

            moviesTable.close()
        }

        test("limit records for testing") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable = MoviesTable(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        limit(1)
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(false)

            val records = destTable.findAll()

            records.size shouldBe 1

            val anonymizedRecord = records[0]
            anonymizedRecord["MOVIE_ID"] shouldBe 1
            anonymizedRecord["TITLE"] shouldBe "MY VALUE"
            anonymizedRecord["RELEASE_DATE"] shouldBe LocalDate.of(1999, 5, 2)
            anonymizedRecord["GENRE"].toString() should match("[a-zA-Z]+")

            sourceTable.close()
            destTable.close()
        }

        test("error handling in case of destination table doesn't exists") {
            DataAnonTestLogHandler.records.clear()

            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_new", "", "")

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(false)

            val errors = DataAnonTestLogHandler.records.filter { it.level.intValue() > Level.INFO.intValue() }
            errors.size shouldBe 1
            errors[0].message should haveSubstring("Table \"MOVIES\" not found")

            sourceTable.close()
        }


        test("error handling in case of source table doesn't exists") {
            DataAnonTestLogHandler.records.clear()

            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies_source", "", "")

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable = MoviesTable(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        whitelist("MOVIE_ID", "RELEASE_DATE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                        anonymize("GENRE").using(FixedString("Action"))
                    }.execute(false)

            val records = destTable.findAll()
            records.size shouldBe 0

            val errors = DataAnonTestLogHandler.records.filter { it.level.intValue() > Level.INFO.intValue() }
            errors.size shouldBe 1
            errors[0].message should haveSubstring("Table \"MOVIES\" not found")

            destTable.close()
        }

        test("error handling in case of INSERT statement error exceeds allowed errors") {
            DataAnonTestLogHandler.records.clear()

            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Really Long Genre To be fail 1", LocalDate.of(1999, 5, 2))
                    .insert(2, "Movie 2", "Really Long Genre To be fail 2", LocalDate.of(2005, 5, 2))
                    .insert(3, "Movie 3", "Action", LocalDate.of(2005, 5, 2))
                    .insert(4, "Movie 4", "Really Long Genre To be fail 3", LocalDate.of(2005, 5, 2))
                    .insert(5, "Movie 5", "Really Long Genre To be fail 4", LocalDate.of(2005, 5, 2))
                    .insert(6, "Movie 6", "Action", LocalDate.of(2005, 5, 2))

            val destDbConfig = DbConfig("jdbc:h2:mem:movies_dest", "", "")
            val destTable    = MoviesTableHavingGenreSize10(destDbConfig)

            Whitelist(sourceDbConfig, destDbConfig)
                    .table("MOVIES") {
                        writeBatchSize(2)
                        allowedErrors(3)
                        whitelist("MOVIE_ID", "RELEASE_DATE", "GENRE")
                        anonymize("TITLE").using(FixedString("MY VALUE"))
                    }.execute(false)

            val records = destTable.findAll()
            records.size should beLessThan(3)

            val errors = DataAnonTestLogHandler.records.filter { it.level.intValue() > Level.INFO.intValue() }
            errors.size shouldBe 6
            errors[0].message should haveSubstring("Value too long for column \"GENRE VARCHAR2(10)\"")
            errors[1].message should haveSubstring("Value too long for column \"GENRE VARCHAR2(10)\"")
            errors[2].message should haveSubstring("Value too long for column \"GENRE VARCHAR2(10)\"")
            errors[3].message should haveSubstring("Value too long for column \"GENRE VARCHAR2(10)\"")
            errors[4].message should haveSubstring("Total number of errors occurred is 4 for table MOVIES exceeds allowed 3")
            errors[5].message should haveSubstring("Too many errors while processing table MOVIES. Exceeds 3 errors hence terminating table processing.")

            destTable.close()
            sourceTable.close()
        }
    }
}