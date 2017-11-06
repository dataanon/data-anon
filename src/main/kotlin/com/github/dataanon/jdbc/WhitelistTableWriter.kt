package com.github.dataanon.jdbc

import com.github.dataanon.Table

class WhitelistTableWriter(dbConfig: Map<String, Any>, table: Table, totalNoOfRecords: Long):
        TableWriter(dbConfig, table, totalNoOfRecords) {

    override fun buildPreparedStatement(): String {
        val sql = StringBuffer("INSERT INTO ${table.name}(")
        sql.append(table.whitelist.joinToString(", ")).append(", ")
        sql.append(table.columnsToBeAnonymized.joinToString(", ") { c -> c.name })
        sql.append(") VALUES(")
        sql.append(table.whitelist.joinToString(",") { "?" }).append(",")
        sql.append(table.columnsToBeAnonymized.joinToString(",") { "?" })
        sql.append(")")
        return sql.toString()
    }

    override fun orderedFieldsInStmt(): List<String> {
        val fields = table.whitelist.toMutableList()
        table.columnsToBeAnonymized.forEach { c -> fields.add(c.name) }
        return fields
    }

}