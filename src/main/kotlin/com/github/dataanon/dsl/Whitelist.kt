package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.WhitelistTableWriter
import reactor.core.publisher.Flux

class Whitelist(private val sourceDbConfig: Map<String, Any>, private val destDbConfig: Map<String, Any>) : Strategy() {

    override fun execute() {
        val reader = TableReader(sourceDbConfig, table)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(WhitelistTableWriter(destDbConfig, table, reader.totalNoOfRecords()))
    }

}