package com.github.dataanon.dsl

import com.github.dataanon.Record
import com.github.dataanon.Table

abstract class Strategy {
    protected lateinit var table: Table

    abstract fun execute()

    protected fun anonymize(record: Record): Record {
        table.columnsToBeAnonymized.forEach { c ->
            val field = record.find(c.name)
            field.newValue = c.strategy.anonymize(field, record)
        }
        return record
    }

}