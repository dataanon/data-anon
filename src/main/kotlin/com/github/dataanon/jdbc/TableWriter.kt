package com.github.dataanon.jdbc

import com.github.dataanon.Record
import com.github.dataanon.Table
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarStyle
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement


abstract class TableWriter(dbConfig: Map<String, Any>, protected val table: Table, totalNoOfRecords: Long) : BaseSubscriber<Record>() {
    private var conn: Connection = DriverManager.getConnection(dbConfig["url"] as String, dbConfig["user"] as String, dbConfig["password"] as String)
    private lateinit var stmt: PreparedStatement
    private lateinit var fields: List<String>
    val pb = ProgressBar(table.name, totalNoOfRecords, ProgressBarStyle.ASCII)
    var batchIndex = 0

    init {
        conn.autoCommit = false
    }

    abstract fun buildPreparedStatement(): String

    override fun hookOnSubscribe(subscription: Subscription?) {
        pb.start()
        val sql = buildPreparedStatement()
        println(sql)
        this.stmt = conn.prepareStatement(sql)
        this.fields = orderedFieldsInStmt()
        request(1)
    }

    override fun hookOnNext(record: Record) {
        batchIndex++
        fields.forEachIndexed { i, f ->
            val field = record.find(f)
            stmt.setObject(i + 1, field.newValue)
        }
        stmt.addBatch()
        if (batchIndex % 1000 == 0) {
            stmt.executeBatch()
            conn.commit()
            stmt.clearBatch()
            batchIndex = 0
        }
        pb.step()
        request(1)
    }

    override fun hookFinally(type: SignalType?) {
        pb.stop()
        stmt.executeBatch()
        conn.commit()
        stmt.clearBatch()
        stmt.close()
        conn.close()
    }

    abstract fun orderedFieldsInStmt(): List<String>
}