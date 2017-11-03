package com.github.dataanon.jdbc

import com.github.dataanon.Columns
import com.github.dataanon.Field
import com.github.dataanon.Record
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

class BlacklistTableReader(dbConfig: Map<String, String>, name: String, private val columns: Columns, private val primaryKey: Array<String>) : Iterator<Record> {
    private var conn: Connection = DriverManager.getConnection(dbConfig["url"], dbConfig["user"], dbConfig["password"])
    private var rs: ResultSet
    private var index = 0

    init {
        val stmt = conn.createStatement()
        val sql = "SELECT " + primaryKey.joinToString(",") + ", " + columns.names().joinToString(",") + " FROM " + name
        rs = stmt.executeQuery(sql)
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
        val fields = mutableListOf<Field>()
        columns.forEach { c ->
            fields.add(fieldFromResultSet(c.name))
        }
        val primaryKeyFields = mutableListOf<Field>()
        primaryKey.forEach { c ->
            primaryKeyFields.add(fieldFromResultSet(c))
        }
        return Record(fields, primaryKeyFields, index)
    }

    private fun fieldFromResultSet(columnName: String): Field {
        val value = rs.getObject(columnName)
        return Field(columnName, value::class.toString(), value, value)
    }
}