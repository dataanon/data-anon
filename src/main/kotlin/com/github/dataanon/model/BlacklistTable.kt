package com.github.dataanon.model

class BlacklistTable(name: String, val primaryKey: List<String>) : Table(name) {

    override fun generateWriteQuery(): String =
        StringBuilder("UPDATE $name SET ").apply {
            append(columnNames().joinToString(", ") { " $it = ? " })
            append(" WHERE ")
            append(primaryKey.joinToString(" AND ") { " $it = ? " })
        }.toString()

    override fun allColumns(): List<String> {
        val fields = columnNames().toMutableList()
        fields.addAll(primaryKey)
        return fields
    }

    override fun allColumnObjects(): List<Column> {
        val columns = columnNames().map { Column(name = it, isKey = false) }
        val keys = primaryKey.map { Column(name = it, isKey = true) }
        return columns.plus(keys)
    }
}