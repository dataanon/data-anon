package com.github.dataanon.model

import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.CopyAnonymizationStrategy
import com.github.dataanon.utils.DefaultAnonymizationStrategies

abstract class Table(val name: String) {
    private val columnStrategyContainer = mutableMapOf<String, ColumnStrategy>()
    internal val properties = mutableMapOf<String, Any>()
    internal var whereCondition: Any? = null
    internal var limit = -1
    internal var allowedErrors = 1000
    internal var batchSize = 1000

    fun anonymize(columnName: String): ColumnStrategy {
        val columnStrategy = ColumnStrategy()
        columnStrategyContainer[columnName] = columnStrategy
        return columnStrategy
    }

    fun where(condition: Any): Table {
        this.whereCondition = condition
        return this
    }

    fun limit(limit: Int): Table {
        this.limit = limit
        return this
    }

    fun writeBatchSize(size: Int): Table {
        this.batchSize = size
        return this
    }

    fun allowedErrors(count: Int): Table {
        this.allowedErrors = count
        return this
    }

    protected fun columnNames() = columnStrategyContainer.keys.toList()

    internal fun execute(record: Record): Record {
        columnStrategyContainer.forEach { columnName, columnStrategy ->
            val field = record.find(columnName)
            field.newValue = columnStrategy.anonymize(field, record)
        }
        return record
    }

    abstract internal fun generateWriteQuery(): String

    abstract internal fun allColumns(): List<String>

    abstract fun allColumnObjects(): List<Column>


    inner class ColumnStrategy {
        private lateinit var anonymizationStrategy: AnonymizationStrategy<*>

        fun <T : Any> using(strategy: AnonymizationStrategy<T>) {
            anonymizationStrategy = strategy
        }

        internal fun anonymize(field: Field<Any>, record: Record) = findAnonymizationStrategy(field).anonymize(field, record)

        private fun findAnonymizationStrategy(field: Field<Any>) = if (field.isNull()) nullAnonymizationStrategy()
        else nonNullAnonymizationStrategy(field)

        private fun nullAnonymizationStrategy() = CopyAnonymizationStrategy<NullValue>() as AnonymizationStrategy<Any>

        private fun nonNullAnonymizationStrategy(field: Field<Any>) = try {
            anonymizationStrategy as AnonymizationStrategy<Any>
        } catch (ex: UninitializedPropertyAccessException) {
            DefaultAnonymizationStrategies.getAnonymizationStrategy(field.oldValue::class) as AnonymizationStrategy<Any>
        }
    }
}
