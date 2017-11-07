package com.github.dataanon.dsl

import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.strategy.AnonymizationStrategy

abstract class Strategy {
    protected lateinit var table: Table

    protected fun anonymize(record: Record): Record {
        table.columnsToBeAnonymized.forEach { k, v ->
            val field = record.find(k)
            field.newValue = (v as AnonymizationStrategy<Any>).anonymize(field, record)
        }
        return record
    }

    abstract fun execute(limit: Long = -1, progressBar: Boolean = true)
}