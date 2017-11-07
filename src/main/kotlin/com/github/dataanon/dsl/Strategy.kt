package com.github.dataanon.dsl

import com.github.dataanon.Record
import com.github.dataanon.Table

abstract class Strategy {
    protected lateinit var table: Table

    protected fun anonymize(record: Record): Record {
        table.columnsToBeAnonymized.forEach { k, v ->
            val field = record.find(k)
            field.newValue = v.anonymize(field, record)
        }
        return record
    }

    abstract fun execute(limit: Long = -1, progressBar: Boolean = true)
}