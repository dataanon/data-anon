package com.github.dataanon

class BlacklistTable(name: String, val primaryKey: List<String>) : Table(name) {

    override fun generateWriteStatement(): String =
            StringBuilder("UPDATE $name SET ").apply {
                append(columnsToBeAnonymized.keys.joinToString(", ") { c -> " $c = ? " })
                append(" WHERE ")
                append(primaryKey.joinToString(" AND ") { k -> " $k = ? " })
            }.toString()


    override fun allColumns(): List<String> {
        val fields = columnsToBeAnonymized.keys.toMutableList()
        fields.addAll(primaryKey)
        return fields
    }


}