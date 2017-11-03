package com.github.dataanon.jdbc

import com.github.dataanon.Columns
import com.github.dataanon.Record
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

class WhitelistTableWriter(dbConfig: Map<String, String>, tableName: String, private val columns: Columns, private val whitelist: Array<String>) : BaseSubscriber<Record>() {
    private val conn: Connection = DriverManager.getConnection(dbConfig["url"], dbConfig["user"], dbConfig["password"])
    private val stmt: PreparedStatement

    init {
        val sql = StringBuffer("INSERT INTO $tableName(")
        sql.append(whitelist.joinToString(", ")).append(", ")
        sql.append(columns.joinToString(", ") { c -> c.name })
        sql.append(") VALUES(")
        sql.append(whitelist.joinToString(",") { "?" }).append(",")
        sql.append(columns.joinToString(",") { "?" })
        sql.append(")")
        println(sql)
        stmt = conn.prepareStatement(sql.toString())
    }

    override fun hookOnSubscribe(subscription: Subscription?) {
        request(1)
    }

    override fun hookOnNext(record: Record) {
        whitelist.forEachIndexed { i, p ->
            val field = record.find(p)
            stmt.setObject(i + 1, field.newValue)
        }
        columns.forEachIndexed { i, c ->
            val field = record.find(c.name)
            stmt.setObject(whitelist.size + i+1, field.newValue)
        }
        stmt.executeUpdate()
        request(1)
    }

    override fun hookFinally(type: SignalType?) {
        stmt.close()
        conn.close()
    }
}