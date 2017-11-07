package com.github.dataanon.jdbc

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.PreparedStatement


class TableWriter(dbConfig: DbConfig, private val table: Table, private val progressBar: ProgressBarGenerator) : BaseSubscriber<Record>() {
    private val conn = dbConfig.connection()
    private val BATCH_COUNT = 1000
    private lateinit var stmt: PreparedStatement
    private lateinit var fields: List<String>
    private var batchIndex = 0

    init {
        conn.autoCommit = false
    }

    override fun hookOnSubscribe(subscription: Subscription?) {
        progressBar.start()
        val sql = table.generateWriteQuery()
        println(sql)
        this.stmt   = conn.prepareStatement(sql)
        this.fields = table.allColumns()
        request(1)
    }

    override fun hookOnNext(record: Record) {
        batchIndex++

        fun executeBatch() {
            stmt.addBatch()

            if (batchIndex % BATCH_COUNT == 0) {
                stmt.executeBatch()
                conn.commit()
                stmt.clearBatch()
                batchIndex = 0
            }
        }
        fun setStatementParameters(record: Record) = fields
                                                        .map { record.find(it) }
                                                        .forEachIndexed { index, field -> stmt.setObject(index + 1, field.newValue)}

        setStatementParameters(record)
        executeBatch()
        progressBar.step()
        request(1)
    }

    override fun hookFinally(type: SignalType?) {
        progressBar.stop()
        stmt.executeBatch()
        conn.commit()
        stmt.clearBatch()
        stmt.close()
        conn.close()
    }
}