package com.github.dataanon.model

import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.string.FixedString

abstract class Table(val name: String) {
    val columnsToBeAnonymized = mutableMapOf<String,Any>()

    fun anonymize(columnName: String): Column {
        val column = Column(columnName)
        columnsToBeAnonymized[columnName] = FixedString("DEFAULT VALUE")
        return column
    }

    // TODO: remove from DSL
    internal fun execute(record: Record): Record {
        columnsToBeAnonymized.forEach { k, v ->
            val field = record.find(k)
            field.newValue = (v as AnonymizationStrategy<Any>).anonymize(field, record)
        }
        return record
    }

    internal fun generateSelectQuery(limit: Long): String {

        val selectClause = "SELECT "
        val columnSelectionClause = allColumns().joinToString(",")
        val fromClause = " FROM $name "
        val limitClause = if(limit > 0) " LIMIT $limit " else ""

        return selectClause + columnSelectionClause + fromClause + limitClause
    }

    abstract internal fun generateWriteQuery(): String

    abstract internal fun allColumns(): List<String>

    inner class Column(private val name: String) {
        fun <T: Any> using(strategy: AnonymizationStrategy<T>) {
            columnsToBeAnonymized[name] = strategy
        }
    }
}