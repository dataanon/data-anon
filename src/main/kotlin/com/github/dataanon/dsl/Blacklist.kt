package com.github.dataanon.dsl

import com.github.dataanon.jdbc.BlacklistTableWriter
import com.github.dataanon.jdbc.TableReader
import reactor.core.publisher.Flux

class Blacklist(private val dbConfig: Map<String, String>): Strategy() {

    override fun execute() {
        val reader = TableReader(dbConfig, tableName, anonymizer.columns, anonymizer.primaryKey)
        Flux.fromIterable(Iterable { reader })
                .map(this::anonymize)
                .subscribe(BlacklistTableWriter(dbConfig, tableName, reader.totalNoOfRecords(), anonymizer.columns, anonymizer.primaryKey))
    }


}