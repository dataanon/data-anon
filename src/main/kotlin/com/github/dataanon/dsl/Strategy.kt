package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.Flux

abstract class Strategy {
    protected var tables = mutableListOf<Table>()

    fun execute(limit: Long = -1, progressBarEnabled: Boolean = true) {
        tables.forEach { table ->
            val reader = TableReader(sourceDbConfig(), table, limit)
            val writer = TableWriter(destDbConfig(), table, ProgressBarGenerator(progressBarEnabled, table.name, { reader.totalNoOfRecords() }))

            Flux.fromIterable(Iterable { reader })
                    .map { table.execute(it) }
                    .subscribe(writer)
        }
    }

    abstract protected fun sourceDbConfig(): DbConfig
    abstract protected fun destDbConfig(): DbConfig
}