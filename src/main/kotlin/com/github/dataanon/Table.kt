package com.github.dataanon

import com.github.dataanon.strategies.AnonymizationStrategy
import com.github.dataanon.strategies.DefaultStringStrategy

abstract class Table(val name: String) {
    var columnsToBeAnonymized = mutableMapOf<String,AnonymizationStrategy>()

    fun anonymize(columnName: String): Column {
        val column = Column(columnName)
        columnsToBeAnonymized[columnName] = DefaultStringStrategy()
        return column
    }

    fun generateSelectQuery(recordsLimit: Long?): String {

        fun selectClause() = "SELECT "
        fun columnSelectionClause() = allColumns().joinToString(",")
        fun fromClause() = " FROM $name "
        fun limitClause() = if(recordsLimit != null) " LIMIT $recordsLimit " else ""

        return selectClause() + columnSelectionClause() + fromClause() + limitClause()
    }

    abstract fun generateWriteQuery(): String

    abstract fun allColumns(): List<String>

    inner class Column(private val name: String) {
        fun using(strategy: AnonymizationStrategy) {
            columnsToBeAnonymized[name] = strategy
        }
    }
}