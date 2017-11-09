package com.github.dataanon.dsl

import com.github.dataanon.jdbc.TableReader
import com.github.dataanon.jdbc.TableWriter
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

abstract class Strategy {
    protected val tables = mutableListOf<Table>()

    fun execute(limit: Long = -1, progressBarEnabled: Boolean = true) {
        Flux.fromIterable(tables)
                .parallel()
                .runOn(Schedulers.parallel())
                .log()
                .subscribe { table -> executeOnTable(table, limit, progressBarEnabled) }
    }

    private fun executeOnTable(table: Table, limit: Long, progressBarEnabled: Boolean) {
        val reader      = TableReader(sourceDbConfig(), table, limit)
        val progressBar = ProgressBarGenerator(progressBarEnabled, table.name, { reader.totalNoOfRecords() })
        val writer      = TableWriter(destDbConfig(), table, progressBar)

        Flux.fromIterable(Iterable { reader }).map { table.execute(it) }.subscribe(writer)
    }

    abstract protected fun sourceDbConfig(): DbConfig
    abstract protected fun destDbConfig():   DbConfig
}