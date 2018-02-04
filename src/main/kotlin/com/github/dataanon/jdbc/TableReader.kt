package com.github.dataanon.jdbc

import com.github.dataanon.model.*
import java.sql.ResultSet

class TableReader(dbConfig: DbConfig, private val table: Table) : Iterator<Record> {
    private val conn = dbConfig.connection()
    private var rs: ResultSet
    private var index = 0

    init {
        val sql  = table.generateSelectQuery()
        val stmt = conn.createStatement()
        if (table.limit >= 1 ) stmt.maxRows = table.limit

        println(sql)
        rs       = stmt.executeQuery(sql)
    }

    fun totalNoOfRecords(): Int     = if (table.limit >= 1) table.limit else getTotalRecords()

    override fun hasNext(): Boolean  = if (rs.next()) true else closeConnection()

    override fun next(): Record {
        index++
        return Record(table.allColumns().map { toField(it) }, index)
    }

    private fun toField(columnName: String)     = Field(columnName, columnValue(columnName))

    private fun columnValue(columnName: String) = rs.getObject(columnName) ?: NullValue

    private fun getTotalRecords(): Int {
        val rs = conn.createStatement().executeQuery(table.generateCountQuery())
        rs.next()
        val count = rs.getInt(1)
        rs.close()
        return count
    }

    private fun closeConnection(): Boolean {
        rs.close()
        conn.close()
        return false
    }
}