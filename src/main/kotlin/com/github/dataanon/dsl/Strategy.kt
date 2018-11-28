package com.github.dataanon.dsl

import com.github.dataanon.db.TableReader
import com.github.dataanon.db.TableWriter
import com.github.dataanon.db.jdbc.JdbcDbConfig
import com.github.dataanon.db.jdbc.JdbcTableReader
import com.github.dataanon.db.jdbc.JdbcTableWriter
import com.github.dataanon.db.mongodb.MongoDbConfig
import com.github.dataanon.db.mongodb.MongoTableReader
import com.github.dataanon.db.mongodb.MongoTableWriter
import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import reactor.core.publisher.toFlux
import reactor.core.scheduler.Schedulers
import java.lang.UnsupportedOperationException
import java.util.concurrent.CountDownLatch
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

abstract class Strategy {
    private val logger = Logger.getLogger(Strategy::class.java.name)
    protected val tables = mutableListOf<Table>()

    abstract protected fun sourceDbConfig(): DbConfig
    abstract protected fun destDbConfig(): DbConfig

    @JvmOverloads
    fun execute(progressBarEnabled: Boolean = true) {
        val inputStream = Strategy::class.java.classLoader.getResourceAsStream("logging.properties")
        LogManager.getLogManager().readConfiguration(inputStream)
        val latch = CountDownLatch(tables.size)

        tables.toFlux()
                .parallel(tables.size)
                .runOn(Schedulers.parallel())
                .subscribe { table -> executeOnTable(table, progressBarEnabled, latch) }

        latch.await()
    }

    private fun executeOnTable(table: Table, progressBarEnabled: Boolean, latch: CountDownLatch) {
        try {
            val reader = getTableReader(sourceDbConfig(), table)
            val progressBar = ProgressBarGenerator(progressBarEnabled, table.name, { reader.totalNoOfRecords() })

            // TODO: Writer might be a {@link org.reactivestreams.Processor} instead of passing progressBar and onFinally callback
            val writer = getTableWriter(sourceDbConfig(), table, progressBar) {
                latch.countDown()
            }

            reader.toFlux().map { table.execute(it) }.subscribe(writer)

        } catch (t: Throwable) {
            logger.log(Level.SEVERE,"Error processing table '${table.name}': ${t.message}",t)
            latch.countDown()
        }
    }

    private fun getTableReader(dbConfig: DbConfig, table: Table) : TableReader {
        return when {
            dbConfig.uri.startsWith("jdbc") -> JdbcTableReader(dbConfig as JdbcDbConfig, table)
            dbConfig.uri.startsWith("mongodb") -> MongoTableReader(dbConfig as MongoDbConfig, table)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun getTableWriter(dbConfig: DbConfig, table: Table, progressBar: ProgressBarGenerator, onFinally: (() -> Unit)? = null) : TableWriter {
        return when {
            dbConfig.uri.startsWith("jdbc") -> JdbcTableWriter(destDbConfig() as JdbcDbConfig, table, progressBar, onFinally)
            dbConfig.uri.startsWith("mongodb") -> MongoTableWriter(destDbConfig() as MongoDbConfig, table, progressBar, onFinally)
            else -> throw UnsupportedOperationException()
        }
    }
}
