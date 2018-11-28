package com.github.dataanon.support.mongodb

import com.github.dataanon.db.mongodb.MongoDbConfig
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.time.Instant

class MoviesCollection(dbConfig: MongoDbConfig) {

    private val conn: MongoClient = MongoClients.create(dbConfig.connectionString)
    private val database: MongoDatabase = conn.getDatabase(dbConfig.connectionString.database)
    private val collection: MongoCollection<Document> = database.getCollection("movies")

    fun insert(movieId: Int, title: String, genre: String, releaseDate: Instant): MoviesCollection {
        collection.insertOne(Document()
                .append("movie_id", movieId)
                .append("title", title)
                .append("genre", genre)
                .append("release_date", releaseDate)
        ).toMono().block()

        return this
    }

    fun findAll() : Publisher<Document> {
        return collection.find()
    }

    fun close() {
        conn.close()
    }

}
