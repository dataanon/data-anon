package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.WhitelistTable
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.Flux

class Whitelist(private val sourceDbConfig: DbConfig, private val destDbConfig: DbConfig) : Strategy() {

    fun table(tableName: String, init: WhitelistTable.() -> Unit): Strategy {
        table = WhitelistTable(tableName)
        (table as WhitelistTable).init()
        return this
    }

    override fun execute(limit: Long, progressBar: Boolean) {
        val reader = TableReader(sourceDbConfig, table, limit)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(TableWriter(destDbConfig, table, ProgressBarGenerator(progressBar, table.name, {reader.totalNoOfRecords()})))
    }
}