package com.github.dataanon.model

class WhitelistTable(name: String) : Table(name) {
    private val whitelist: ArrayList<String> = arrayListOf()

    fun whitelist(vararg columns: String) {
        whitelist.addAll(columns)
    }

    override fun generateWriteQuery(): String =
            StringBuilder("INSERT INTO $name(").apply {
                val columns = whitelist + columnNames()
                append(columns.joinToString(", "))
                append(") VALUES(")
                append(columns.joinToString(",") { "?" })
                append(")")
            }.toString()

    override fun allColumns(): List<String> {
        val fields = whitelist.toMutableList()
        fields.addAll(columnNames())
        return fields
    }
}