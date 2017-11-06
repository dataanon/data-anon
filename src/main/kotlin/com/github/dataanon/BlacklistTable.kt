package com.github.dataanon

class BlacklistTable(name: String, val primaryKey: List<String>) : Table(name) {

    override fun generateWriteQuery(): String =
            StringBuilder("UPDATE $name SET ").apply {
                append(columnsToBeAnonymized.keys.joinToString(", ") { " $it = ? " })
                append(" WHERE ")
                append(primaryKey.joinToString(" AND ") { " $it = ? " })
            }.toString()


    override fun allColumns(): List<String> {
        val fields = columnsToBeAnonymized.keys.toMutableList()
        fields.addAll(primaryKey)
        return fields
    }
}