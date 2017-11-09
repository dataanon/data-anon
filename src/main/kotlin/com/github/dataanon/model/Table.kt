package com.github.dataanon.model

import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.DefaultAnonymizationStrategies

abstract class Table(val name: String) {
    private val columnStrategyContainer = mutableMapOf<String, ColumnStrategy>()
    var whereCondition = ""

    fun anonymize(columnName: String): ColumnStrategy {
        val columnStrategy = ColumnStrategy()
        columnStrategyContainer[columnName] = columnStrategy
        return columnStrategy
    }

    fun where(condition: String) {
        this.whereCondition = condition
    }

    protected fun columnNames() = columnStrategyContainer.keys.toList()

    internal fun execute(record: Record): Record {
        columnStrategyContainer.forEach { columnName, columnStrategy ->
            val field = record.find(columnName)
            field.newValue = columnStrategy.anonymize(field, record)
        }
        return record
    }

    internal fun generateSelectQuery(limit: Long): String {
        val columns = allColumns().joinToString(",")
        val limitClause = if(limit > 0) " LIMIT $limit " else ""
        val whereClause = if(whereCondition.isNotEmpty()) " WHERE $whereCondition " else ""

        return "SELECT $columns FROM $name $whereClause $limitClause"
    }

    abstract internal fun generateWriteQuery(): String

    abstract internal fun allColumns(): List<String>

    inner class ColumnStrategy {
        private lateinit var anonymizationStrategy: AnonymizationStrategy<*>

        fun <T: Any> using(strategy: AnonymizationStrategy<T>) {
            anonymizationStrategy = strategy
        }

        internal fun anonymize(field: Field<Any>, record: Record) = anonymizationStrategy(field).anonymize(field, record)

        private fun anonymizationStrategy(field: Field<Any>) = try {
                                                    anonymizationStrategy as AnonymizationStrategy<Any>
                                                  }
                                                  catch (ex: UninitializedPropertyAccessException){
                                                    DefaultAnonymizationStrategies.getAnonymizationStrategy(field.oldValue::class) as AnonymizationStrategy<Any>
                                                  }
    }
}