package com.github.dataanon.jdbc

import com.github.dataanon.Columns

class WhitelistTableWriter(dbConfig: Map<String, Any>, tableName: String, totalNoOfRecords: Long,
                           private val columns: Columns, private val whitelist: Array<String>) :
        TableWriter(dbConfig, tableName, totalNoOfRecords) {

    override fun buildPreparedStatement(): String {
        val sql = StringBuffer("INSERT INTO $tableName(")
        sql.append(whitelist.joinToString(", ")).append(", ")
        sql.append(columns.joinToString(", ") { c -> c.name })
        sql.append(") VALUES(")
        sql.append(whitelist.joinToString(",") { "?" }).append(",")
        sql.append(columns.joinToString(",") { "?" })
        sql.append(")")
        return sql.toString()
    }

    override fun orderedFieldsInStmt(): List<String> {
        val fields = whitelist.toMutableList()
        columns.forEach { c -> fields.add(c.name) }
        return fields
    }

}