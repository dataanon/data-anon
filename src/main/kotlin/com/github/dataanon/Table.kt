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

    abstract fun generateWriteStatement(): String

    abstract fun allColumns(): List<String>

    inner class Column(private val name: String) {
        fun using(strategy: AnonymizationStrategy) {
            columnsToBeAnonymized[name] = strategy
        }
    }

}



