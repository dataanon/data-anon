package com.github.dataanon.jdbc

import com.github.dataanon.Record
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

abstract class TableWriter(dbConfig: Map<String, Any>, protected val tableName: String) : BaseSubscriber<Record>() {
    private var conn: Connection = DriverManager.getConnection(dbConfig["url"] as String, dbConfig["user"] as String, dbConfig["password"] as String)
    private lateinit var stmt: PreparedStatement
    private lateinit var fields: List<String>

    abstract fun buildPreparedStatement(): String

    override fun hookOnSubscribe(subscription: Subscription?) {
        val sql = buildPreparedStatement()
        println(sql)
        this.stmt = conn.prepareStatement(sql)
        this.fields = orderedFieldsInStmt()
        request(1)
    }

    override fun hookOnNext(record: Record) {
        fields.forEachIndexed { i, f ->
            val field = record.find(f)
            stmt.setObject(i + 1, field.newValue)
        }
        stmt.executeUpdate()
        request(1)
    }

    override fun hookFinally(type: SignalType?) {
        stmt.close()
        conn.close()
    }

    abstract fun orderedFieldsInStmt(): List<String>
}