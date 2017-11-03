package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.WhitelistTableWriter
import reactor.core.publisher.Flux

class Whitelist(private val sourceDbConfig: Map<String, Any>, private val destDbConfig: Map<String, Any>) : Strategy() {

    override fun execute() {
        Flux.fromIterable(Iterable { TableReader(sourceDbConfig, tableName, anonymizer.columns, anonymizer.whitelist) })
                .map(this::anonymize)
                .subscribe(WhitelistTableWriter(destDbConfig, tableName, anonymizer.columns, anonymizer.whitelist))
    }

}