package com.github.dataanon.jdbc

import com.github.dataanon.Columns

class BlacklistTableWriter(dbConfig: Map<String, Any>, tableName: String,
                           private val columns: Columns, private val primaryKey: Array<String>) :
        TableWriter(dbConfig,tableName) {

    override fun buildPreparedStatement(): String {
        val sql = StringBuffer("UPDATE $tableName SET ")
        sql.append(columns.joinToString(", ") { c -> " ${c.name} = ? " })
        sql.append(" WHERE ")
        sql.append(primaryKey.joinToString(" AND ") { k -> " $k = ? " })
        return sql.toString()
    }

    override fun orderedFieldsInStmt(): List<String> {
        val fields = columns.map { c -> c.name }.toMutableList()
        primaryKey.forEach { p -> fields.add(p) }
        return fields
    }

}