package com.github.dataanon.jdbc

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import java.sql.ResultSet

class TableReader(dbConfig: DbConfig, private val table: Table, private val limit: Long) : Iterator<Record> {
    private val conn = dbConfig.connection()
    private var rs: ResultSet
    private var index = 0

    init {
        val stmt = conn.createStatement()
        val sql  = table.generateSelectQuery(limit)

        println(sql)
        rs       = stmt.executeQuery(sql)
    }

    fun totalNoOfRecords(): Long     = if (limit > 1) limit else getTotalRecords()

    override fun hasNext(): Boolean  = if (rs.next()) true else closeConnection()

    override fun next(): Record {
        index++
        return Record(table.allColumns().map { Field(it, rs.getObject(it)) }, index)
    }

    private fun getTotalRecords(): Long {
        val rs = conn.createStatement().executeQuery("SELECT COUNT(1) FROM ${table.name}")
        rs.next()
        val count = rs.getLong(1)
        rs.close()
        return count
    }

    private fun closeConnection(): Boolean {
        rs.close()
        conn.close()
        return false
    }
}