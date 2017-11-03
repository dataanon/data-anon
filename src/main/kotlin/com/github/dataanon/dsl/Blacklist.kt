package com.github.dataanon.dsl

import com.github.dataanon.jdbc.BlacklistTableWriter
import com.github.dataanon.jdbc.TableReader
import reactor.core.publisher.Flux

class Blacklist(private val dbConfig: Map<String, String>): Strategy() {

    override fun execute() {
        Flux.fromIterable(Iterable { TableReader(dbConfig, tableName, anonymizer.columns, anonymizer.primaryKey) })
                .map(this::anonymize)
                .subscribe(BlacklistTableWriter(dbConfig, tableName, anonymizer.columns, anonymizer.primaryKey))
    }


}