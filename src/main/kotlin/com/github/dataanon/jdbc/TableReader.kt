package com.github.dataanon.jdbc

import com.github.dataanon.DbConfig
import com.github.dataanon.Field
import com.github.dataanon.Record
import com.github.dataanon.Table
import java.sql.ResultSet

class TableReader(dbConfig: DbConfig, private val table: Table, private val limit: Long) : Iterator<Record> {
    private var conn = dbConfig.conn()
    private var rs: ResultSet
    private var index = 0

    init {
        val stmt = conn.createStatement()
        val sql  = table.generateSelectQuery(limit)

        println(sql)
        rs       = stmt.executeQuery(sql)
    }

    fun totalNoOfRecords(): Long {
        if (limit > 1) return limit

        val rs = conn.createStatement().executeQuery("SELECT COUNT(1) FROM ${table.name}")
        rs.next()
        val count = rs.getLong(1)
        rs.close()
        return count
    }

    override fun hasNext(): Boolean {
        val isNext = rs.next()
        if (!isNext) {
            rs.close()
            conn.close()
            return false
        }
        return isNext
    }

    override fun next(): Record {
        index++
        return Record(table.allColumns().map { Field(it, rs.getObject(it)) }, index)
    }
}