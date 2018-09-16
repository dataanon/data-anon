package com.github.dataanon.jdbc

import com.github.dataanon.model.*
import com.github.dataanon.utils.ProgressBarGenerator
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger


class TableWriter(dbConfig: DbConfig, private val table: Table, private val progressBar: ProgressBarGenerator) : BaseSubscriber<Record>() {
    private val logger = Logger.getLogger(TableWriter::class.java.name)

    private val conn = dbConfig.connection()

    private var stmt: PreparedStatement
    private var fields: List<String>

    private var batchIndex = 0
    private var errorCount = 0

    init {
        conn.autoCommit = false
        progressBar.start()
        val sql = table.generateWriteQuery()
        logger.info { "WRITE SQL: $sql" }
        this.stmt = conn.prepareStatement(sql)
        this.fields = table.allColumns()
    }

    override fun hookOnSubscribe(subscription: Subscription?) {
        request(1)
    }

    override fun hookOnError(throwable: Throwable) {
        throw throwable
    }

    override fun hookOnNext(record: Record) {
        fields.map { record.find(it) }.forEachIndexed { index, field -> writeToStatement(index, field) }

        stmt.addBatch()
        batchIndex++
        progressBar.step()

        if (batchIndex % table.batchSize == 0) executeBatchStmt()

        request(1)
    }

    private fun writeToStatement(index: Int, field: Field<Any>) {
        val columnIndex = index + 1
        val newValue = field.newValue
        when (newValue) {
            is NullValue -> stmt.setNull(columnIndex, Types.NULL)
            is LocalDate -> stmt.setDate(columnIndex, Date.valueOf(newValue))
            is LocalDateTime -> stmt.setTimestamp(columnIndex, Timestamp.valueOf(newValue))
            else -> stmt.setObject(columnIndex, newValue)
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
            logger.log(Level.SEVERE, "Error executing batch: ${t.message}", t)
        }
        if (errorCount > table.allowedErrors) {
            logger.severe { "Total number of errors occurred is $errorCount for table ${table.name} exceeds allowed ${table.allowedErrors} ." }
            throw Exception("Too many errors while processing table ${table.name}. Exceeds ${table.allowedErrors} errors hence terminating table processing.")
        }
    }

    override fun hookOnComplete() {
        if (batchIndex > 0) executeBatchStmt()
        if (errorCount > 0) logger.severe { "On Complete total number of errors occurred is $errorCount for table ${table.name}" }
    }

    override fun hookFinally(type: SignalType?) {
        progressBar?.stop()
        stmt?.close()
        conn?.close()
    }

}