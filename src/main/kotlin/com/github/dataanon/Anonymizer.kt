package com.github.dataanon

import com.github.dataanon.jdbc.BlacklistTableReader
import com.github.dataanon.jdbc.BlacklistTableWriter
import reactor.core.publisher.Flux

class Anonymizer(private val dbConfig: Map<String, String>, private val name: String) {
    var columns = Columns()
    var primaryKey: Array<String> = arrayOf("ID")

    fun primaryKey(vararg primaryKey: String) {
        this.primaryKey = primaryKey as Array<String>
    }

    fun anonymize(columnName: String): Column {
        val column = Column(columnName)
        columns.add(column)
        return column
    }

    fun execute() {
        Flux.fromIterable(Iterable { BlacklistTableReader(dbConfig, name, columns, primaryKey) })
                .map(this::anonymize)
                .subscribe(BlacklistTableWriter(dbConfig,name, columns, primaryKey))
    }

    private fun anonymize(record: Record): Record {
        record.fields.forEach { f ->
            val strategy = columns.strategyFor(f.name)
            f.newValue = strategy.anonymize(f, record)
        }
        return record
    }
}