package com.github.dataanon.strategy.list

import com.github.dataanon.Matchers
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.support.MoviesTable
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.FunSpec
import java.time.LocalDate

class PickFromDatabaseUnitTest : FunSpec(), Matchers {

    val emptyRecord: Record = Record(listOf(), 0)

    init {
        test("should return a random value from list") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            val sourceTable = MoviesTable(sourceDbConfig)
                    .insert(1, "Movie 1", "Drama", LocalDate.of(1999, 5, 2))
                    .insert(2, "Movie 2", "Action", LocalDate.of(2005, 5, 2))
                    .insert(3, "Movie 3", "Action", LocalDate.of(2005, 5, 2))
                    .insert(4, "Movie 2", "SciFi", LocalDate.of(2005, 5, 2))

            val strategy  = PickFromDatabase<String>(sourceDbConfig,"SELECT DISTINCT GENRE FROM MOVIES")

            val field     = Field("country", "OldData")
            val anonymized = strategy.anonymize(field, emptyRecord)

            val genre = listOf("Drama", "Action", "SciFi")
            anonymized should beIn(genre)
            sourceTable.close()
        }

        test("should throw IllegalArgumentException given empty list") {
            val sourceDbConfig = DbConfig("jdbc:h2:mem:movies", "", "")
            MoviesTable(sourceDbConfig)
            shouldThrow<IllegalArgumentException> {
                PickFromDatabase<String>(sourceDbConfig,"SELECT DISTINCT GENRE FROM MOVIES")
            }
        }
    }
}