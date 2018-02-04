package com.github.dataanon.model

import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.CopyAnonymizationStrategy
import com.github.dataanon.utils.DefaultAnonymizationStrategies

abstract class Table(val name: String) {
    private val columnStrategyContainer = mutableMapOf<String, ColumnStrategy>()
    internal var whereCondition = ""
    internal var limit = -1

    fun anonymize(columnName: String): ColumnStrategy {
        val columnStrategy = ColumnStrategy()
        columnStrategyContainer[columnName] = columnStrategy
        return columnStrategy
    }

    fun where(condition: String): Table {
        this.whereCondition = " WHERE $condition "
        return this
    }

    fun limit(limit: Int): Table {
        this.limit = limit
        return this
    }

    protected fun columnNames() = columnStrategyContainer.keys.toList()

    internal fun execute(record: Record): Record {
        columnStrategyContainer.forEach { columnName, columnStrategy ->
            val field       = record.find(columnName)
            field.newValue  = columnStrategy.anonymize(field, record)
        }
        return record
    }

    internal fun generateSelectQuery(): String =
            "SELECT ${allColumns().joinToString(",")} FROM $name $whereCondition".trim()

    internal fun generateCountQuery(): String =
            "SELECT COUNT(1) FROM $name $whereCondition".trim()

    abstract internal fun generateWriteQuery(): String

    abstract internal fun allColumns(): List<String>


    inner class ColumnStrategy {
        private lateinit var anonymizationStrategy: AnonymizationStrategy<*>

        fun <T: Any> using(strategy: AnonymizationStrategy<T>) {
            anonymizationStrategy = strategy
        }

        internal fun anonymize(field: Field<Any>, record: Record) = findAnonymizationStrategy(field).anonymize(field, record)

        private fun findAnonymizationStrategy(field: Field<Any>)  = if (field.isNull()) nullAnonymizationStrategy()
                                                                    else                nonNullAnonymizationStrategy(field)

        private fun nullAnonymizationStrategy() = CopyAnonymizationStrategy<NullValue>() as AnonymizationStrategy<Any>

        private fun nonNullAnonymizationStrategy(field: Field<Any>) = try {
                                                            anonymizationStrategy as AnonymizationStrategy<Any>
                                                        }
                                                        catch (ex: UninitializedPropertyAccessException){
                                                            DefaultAnonymizationStrategies.getAnonymizationStrategy(field.oldValue::class) as AnonymizationStrategy<Any>
                                                        }
    }
}