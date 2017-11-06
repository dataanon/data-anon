package com.github.dataanon.dsl

import com.github.dataanon.WhitelistTable
import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import reactor.core.publisher.Flux

class Whitelist(private val sourceDbConfig: Map<String, Any>, private val destDbConfig: Map<String, Any>) : Strategy() {

    fun table(tableName: String, init: WhitelistTable.() -> Unit): Strategy {
        table = WhitelistTable(tableName)
        (table as WhitelistTable).init()
        return this
    }

    override fun execute() {
        val reader = TableReader(sourceDbConfig, table)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(TableWriter(destDbConfig, table, reader.totalNoOfRecords()))
    }

}