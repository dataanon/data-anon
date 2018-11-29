package com.github.dataanon.db.mongodb

import com.github.dataanon.db.TableReader
import com.github.dataanon.model.Field
import com.github.dataanon.model.NullValue
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.reactivestreams.Subscriber
import reactor.core.publisher.toFlux
import java.lang.ClassCastException
import java.time.ZoneId
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

class MongoTableReader(dbConfig: MongoDbConfig, private val table: Table) : TableReader {
    private val logger = Logger.getLogger(MongoTableReader::class.java.name)

    private val conn: MongoClient = MongoClients.create(dbConfig.connectionString)
    private val database: MongoDatabase = conn.getDatabase(dbConfig.connectionString.database)
    private val collection: MongoCollection<Document> = database.getCollection(table.name)

    private val index: AtomicInteger = AtomicInteger(1)

    override fun subscribe(s: Subscriber<in Record>) {
        val findPublisher = when (table.whereCondition is Bson) {
            true -> collection.find(table.whereCondition as Bson)
            false -> collection.find()
        }

        var flux = findPublisher.toFlux().map { doc -> toRecord(doc) }

        if (table.limit > 0) {
            flux = flux.take(table.limit.toLong())
        }

        flux.subscribe(s)
    }

    override fun totalNoOfRecords(): Int {
        // return (collection.countDocuments().toMono().block() ?: 0).toInt()
        // TODO: use non-blocking version
        return 3
    }

    private fun toRecord(document: Document): Record {
        try {
            return Record(table.allColumns().map { toField(document, it) }, index.getAndIncrement())
        } catch (e: ClassCastException) {
            logger.severe(e.message)
            throw e
        }
    }

    private fun toField(document: Document, fieldName: String): Field<Any> {
        return Field(fieldName, toValue(document, fieldName))
    }

    private fun toValue(document: Document, fieldName: String): Any {
        val value = document[fieldName]

        return when (value) {
            // TODO: check more data types.
            is Date -> value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            else -> value ?: NullValue
        }
    }

}
