package com.github.dataanon

class Table(val name: String) {
    var columnsToBeAnonymized = Columns()
    var primaryKey: Array<String> = arrayOf()
    var whitelist: Array<String> = arrayOf()

    fun primaryKey(vararg primaryKey: String) {
        this.primaryKey = primaryKey as Array<String>
    }

    fun whitelist(vararg columnName: String) {
        this.whitelist = columnName as Array<String>
    }

    fun anonymize(columnName: String): Column {
        val column = Column(columnName)
        columnsToBeAnonymized.add(column)
        return column
    }

}