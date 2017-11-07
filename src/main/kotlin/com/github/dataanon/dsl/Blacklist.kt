package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import com.github.dataanon.model.BlacklistTable
import com.github.dataanon.model.DbConfig
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.Flux

class Blacklist(private val dbConfig: DbConfig): Strategy() {

    fun table(tableName: String, primaryKey: List<String>, init: BlacklistTable.() -> Unit): Strategy {
        table = BlacklistTable(tableName,primaryKey)
        (table as BlacklistTable).init()
        return this
    }

    override fun execute(limit: Long, progressBar: Boolean) {
        val reader = TableReader(dbConfig, table, limit)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(TableWriter(dbConfig, table, ProgressBarGenerator(progressBar, table.name, {reader.totalNoOfRecords()})))
    }
}