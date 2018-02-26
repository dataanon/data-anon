package com.github.dataanon.jdbc

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.BatchUpdateException
import java.sql.PreparedStatement
import java.sql.Types
import java.util.logging.Level
import java.util.logging.Logger


class TableWriter(dbConfig: DbConfig, private val table: Table, private val progressBar: ProgressBarGenerator) : BaseSubscriber<Record>() {
    private val logger = Logger.getLogger(TableWriter::class.java.name)

    private val BATCH_COUNT = 1000
    private val TOTAL_ALLOWED_ERRORS = 1000

    private val conn = dbConfig.connection()

    private lateinit var stmt: PreparedStatement
    private lateinit var fields: List<String>

    private var batchIndex = 0
    private var errorCount = 0

    init {
        conn.autoCommit = false
    }

    override fun hookOnSubscribe(subscription: Subscription?) {
        progressBar.start()
        val sql = table.generateWriteQuery()
        logger.info { "WRITE SQL: $sql" }
        this.stmt = conn.prepareStatement(sql)
        this.fields = table.allColumns()
        request(1)
    }

    override fun hookOnNext(record: Record) {

        fields.map { record.find(it) }.forEachIndexed { index, field -> writeToStatement(index, field) }

        stmt.addBatch()
        batchIndex++
        progressBar.step()

        if (batchIndex % BATCH_COUNT == 0) executeBatchStmt()

        request(1)
    }

    private fun writeToStatement(index: Int, field: Field<Any>) {
        when {
            field.isNull() -> stmt.setNull(index + 1, Types.NULL)
            else -> stmt.setObject(index + 1, field.newValue)
        }
    }

    private fun executeBatchStmt() {
        try {
            stmt.executeBatch()
        } catch (t: Throwable) {
            handleError(t)
        } finally {
            conn.commit()
            stmt.clearBatch()
            batchIndex = 0
        }
    }


    private fun handleError(t: Throwable) {
        if (t is BatchUpdateException) {
            t.forEach {
                if (it is BatchUpdateException) return@forEach
                errorCount++
                logger.log(Level.SEVERE, "Individual error messages in BatchUpdateException: ${it.message}", it)
            }
        } else {
            errorCount++
            logger.log(Level.SEVERE, "Error executing batch record: ${t.message}", t)
        }
        if (errorCount > TOTAL_ALLOWED_ERRORS) {
            logger.severe { "Total number of errors occurred is $errorCount for table ${table.name} exceeds allowed $TOTAL_ALLOWED_ERRORS." }
            throw Exception("Too many errors while processing table ${table.name} exceeds $TOTAL_ALLOWED_ERRORS hence terminating table processing.")
        }
    }

    override fun hookOnComplete() {
        if (batchIndex > 0) executeBatchStmt()
        if (errorCount > 0) logger.severe { "On Complete total number of errors occurred is $errorCount for table ${table.name}" }
    }

    override fun hookFinally(type: SignalType?) {
        progressBar.stop()
        stmt.close()
        conn.close()
    }

}