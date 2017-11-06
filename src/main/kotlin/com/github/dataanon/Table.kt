package com.github.dataanon

abstract class Table(val name: String) {
    var columnsToBeAnonymized = mutableListOf<Column>()

    fun anonymize(columnName: String): Column {
        val column = Column(columnName)
        columnsToBeAnonymized.add(column)
        return column
    }

    abstract fun generateWriteStatement(): String

    abstract fun allColumns(): List<String>

}