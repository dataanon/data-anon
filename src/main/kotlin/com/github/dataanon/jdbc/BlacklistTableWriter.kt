package com.github.dataanon.jdbc

import com.github.dataanon.Table

class BlacklistTableWriter(dbConfig: Map<String, Any>, table: Table, totalNoOfRecords: Long) :
        TableWriter(dbConfig, table, totalNoOfRecords) {

    override fun buildPreparedStatement(): String {
        val sql = StringBuffer("UPDATE ${table.name} SET ")
        sql.append(table.columnsToBeAnonymized.joinToString(", ") { c -> " ${c.name} = ? " })
        sql.append(" WHERE ")
        sql.append(table.primaryKey.joinToString(" AND ") { k -> " $k = ? " })
        return sql.toString()
    }

    override fun orderedFieldsInStmt(): List<String> {
        val fields = table.columnsToBeAnonymized.map { c -> c.name }.toMutableList()
        table.primaryKey.forEach { p -> fields.add(p) }
        return fields
    }

}