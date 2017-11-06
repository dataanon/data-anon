package com.github.dataanon.dsl

import com.github.dataanon.BlacklistTable
import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import reactor.core.publisher.Flux

class Blacklist(private val dbConfig: Map<String, String>): Strategy() {

    fun table(tableName: String, primaryKey: List<String>, init: BlacklistTable.() -> Unit): Strategy {
        table = BlacklistTable(tableName,primaryKey)
        (table as BlacklistTable).init()
        return this
    }

    override fun execute() {
        val reader = TableReader(dbConfig, table)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(TableWriter(dbConfig, table, reader.totalNoOfRecords()))
    }
}