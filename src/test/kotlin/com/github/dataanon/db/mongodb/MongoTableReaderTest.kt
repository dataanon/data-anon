package com.github.dataanon.db.mongodb

import com.github.dataanon.model.BlacklistTable
import com.github.dataanon.support.mongodb.MoviesCollection
import io.kotlintest.*
import reactor.test.StepVerifier
import java.time.Instant
import java.util.*

class MongoTableReaderTest : MongoSpec() {
    init {
        test("should return the number of records specified by limit") {
            val (dbConfig, moviesCollection) = prepareDataWith5Movies()
            val blacklistTable = BlacklistTable("movies", listOf("_id", "movie_id")).limit(2)
            val tableReader = MongoTableReader(dbConfig, blacklistTable)

            StepVerifier.create(tableReader)
                    .assertNext {
                        it.find("_id") shouldNotBe null
                        it.find("movie_id") shouldNotBe null
                        shouldThrow<NoSuchElementException> { it.find("title") }
                        shouldThrow<NoSuchElementException> { it.find("genre") }
                        shouldThrow<NoSuchElementException> { it.find("release_date") }
                    }
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()
        }

        test("should return all records given limit is -1") {
            val (dbConfig, moviesCollection) = prepareDataWith5Movies()
            val blacklistTable = BlacklistTable("movies", listOf("_id", "movie_id", "title"))
            val tableReader = MongoTableReader(dbConfig, blacklistTable)

            StepVerifier.create(tableReader)
                    .assertNext {
                        it.find("_id") shouldNotBe null
                        it.find("movie_id") shouldNotBe null
                        it.find("title") shouldNotBe null
                        shouldThrow<NoSuchElementException> { it.find("genre") }
                        shouldThrow<NoSuchElementException> { it.find("release_date") }
                    }
                    .expectNextCount(4)
                    .expectComplete()
                    .verify()
        }
    }

    private fun prepareDataWith5Movies(): Pair<MongoDbConfig, MoviesCollection> {
        val dbConfig = MongoDbConfig("mongodb://$hostAddress:$localPort/" + UUID.randomUUID())
        val moviesCollection = MoviesCollection(dbConfig)
                .insert(1, "Movie 1", "Drama", Instant.ofEpochSecond(925603200))
                .insert(2, "Movie 2", "Action", Instant.ofEpochSecond(1114992000))
                .insert(3, "Movie 3", "Comedy", Instant.ofEpochSecond(1114992000))
                .insert(4, "Movie 4", "Horror", Instant.ofEpochSecond(1114992000))
                .insert(5, "Movie 5", "Fiction", Instant.ofEpochSecond(1114992000))

        return Pair(dbConfig, moviesCollection)
    }

}
