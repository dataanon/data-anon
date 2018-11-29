package com.github.dataanon.db.mongodb

import com.github.dataanon.db.TableWriter
import com.github.dataanon.model.Field
import com.github.dataanon.model.NullValue
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import com.mongodb.reactivestreams.client.*
import org.bson.Document
import reactor.core.publisher.SignalType
import reactor.core.publisher.toMono
import java.util.logging.Logger

class MongoTableWriter(
        dbConfig: MongoDbConfig,
        table: Table,
        progressBar: ProgressBarGenerator,
        onFinally: (() -> Unit)? = null)
    : TableWriter(table, progressBar, onFinally) {

    private val logger = Logger.getLogger(MongoTableWriter::class.java.name)

    private val conn: MongoClient = MongoClients.create(dbConfig.connectionString)
    private val database: MongoDatabase = conn.getDatabase(dbConfig.connectionString.database)
    private val collection: MongoCollection<Document> = database.getCollection(table.name)

    init {
        progressBar.start()
    }

    override fun hookOnNext(record: Record) {
        val document = Document()
        fields.map { record.find(it) }.forEach { field -> appendDocument(document, field) }

        // TODO: used block since tests are failing on the second run. will remove it.
        collection.insertOne(document).toMono().block()

        progressBar.step()

        request(1)
    }

    private fun appendDocument(document: Document, field: Field<Any>) {
        val value: Any? = when (field.newValue) {
            NullValue -> null
            else -> field.newValue
        }
        document.append(field.name, value)
    }

    override fun hookOnComplete() {
        // TODO: log the result including error count
    }

    override fun hookFinally(type: SignalType) {
        progressBar.stop()
        conn.close()
        super.hookFinally(type)
    }

}
