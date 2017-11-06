package com.github.dataanon

class WhitelistTable(name: String) : Table(name) {
    val whitelist: ArrayList<String> = arrayListOf()

    fun whitelist(vararg columns: String) {
        whitelist.addAll(columns)
    }

    override fun generateWriteStatement(): String =
            StringBuilder("INSERT INTO $name(").apply {
                append(whitelist.joinToString(", ")).append(", ")
                append(columnsToBeAnonymized.joinToString(", ") { c -> c.name })
                append(") VALUES(")
                append(whitelist.joinToString(",") { "?" }).append(",")
                append(columnsToBeAnonymized.joinToString(",") { "?" })
                append(")")
            }.toString()

    override fun allColumns(): List<String> {
        val fields = whitelist.toMutableList()
        columnsToBeAnonymized.forEach { c -> fields.add(c.name) }
        return fields
    }


}