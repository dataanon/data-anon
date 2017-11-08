package com.github.dataanon.model

class WhitelistTable(name: String) : Table(name) {
    private val whitelist: ArrayList<String> = arrayListOf()

    fun whitelist(vararg columns: String) {
        whitelist.addAll(columns)
    }

    override fun generateWriteQuery(): String =
            StringBuilder("INSERT INTO $name(").apply {
                append(whitelist.joinToString(", ")).append(", ")
                append(columnNames().joinToString(", "))
                append(") VALUES(")
                append(whitelist.joinToString(",") { "?" }).append(",")
                append(columnNames().joinToString(",") { "?" })
                append(")")
            }.toString()

    override fun allColumns(): List<String> {
        val fields = whitelist.toMutableList()
        fields.addAll(columnNames())
        return fields
    }
}