package com.github.dataanon

class BlacklistTable(name: String, val primaryKey: List<String>) : Table(name) {

    override fun generateWriteStatement(): String =
            StringBuilder("UPDATE $name SET ").apply {
                append(columnsToBeAnonymized.joinToString(", ") { c -> " ${c.name} = ? " })
                append(" WHERE ")
                append(primaryKey.joinToString(" AND ") { k -> " $k = ? " })
            }.toString()


    override fun allColumns(): List<String> {
        val fields = columnsToBeAnonymized.map { c -> c.name }.toMutableList()
        primaryKey.forEach { p -> fields.add(p) }
        return fields
    }


}