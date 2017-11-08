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
}