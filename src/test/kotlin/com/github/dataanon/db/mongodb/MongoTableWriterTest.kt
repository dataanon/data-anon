package com.github.dataanon.db.mongodb

import com.github.dataanon.model.BlacklistTable
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.support.mongodb.MoviesCollection
import com.github.dataanon.utils.ProgressBarGenerator
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import reactor.core.publisher.toFlux
import reactor.test.StepVerifier
import reactor.test.publisher.TestPublisher
import java.util.NoSuchElementException
import java.util.UUID

class MongoTableWriterTest : MongoSpec() {

    init {
        test("should write the document") {
            val (dbConfig, moviesCollection) = getEmptyMoviesCollection()
            val tableName = "movies"
            val blacklistTable = BlacklistTable(tableName, listOf("_id", "movie_id", "title"))
            val progressBar = ProgressBarGenerator(false, tableName) { 1 }

            val writer = MongoTableWriter(dbConfig, blacklistTable, progressBar)

            val publisher = TestPublisher.create<Record>()
            publisher.subscribe(writer)

            publisher
                    .next(Record(listOf(getField("_id"), getField("movie_id"), getField("title")), 1))
                    .complete()

            publisher.wasSubscribed() shouldBe true
            publisher.wasRequested() shouldBe true

            StepVerifier.create(moviesCollection.findAll().toFlux())
                    .assertNext {
                        it["_id"] shouldNotBe null
                        it["movie_id"] shouldNotBe null
                        it["title"] shouldNotBe null
                        it["genre"] shouldBe null
                        it["release_date"] shouldBe null
                    }
                    .expectComplete()
                    .verify()

            moviesCollection.close()
        }
    }

    private fun getEmptyMoviesCollection(): Pair<MongoDbConfig, MoviesCollection> {
        val dbConfig = MongoDbConfig("mongodb://$hostAddress:$localPort/" + UUID.randomUUID())
        val moviesCollection = MoviesCollection(dbConfig)

        return Pair(dbConfig, moviesCollection)
    }

    private fun getField(name: String): Field<Any> = Field(name, UUID.randomUUID().toString())

}
