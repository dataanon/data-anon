package com.github.dataanon.dsl

import com.github.dataanon.Record
import com.github.dataanon.Table

abstract class Strategy {
    protected lateinit var table: Table

    abstract fun execute()

    protected fun anonymize(record: Record): Record {
        table.columnsToBeAnonymized.forEach { k, v ->
            val field = record.find(k)
            field.newValue = v.anonymize(field, record)
        }
        return record
    }

}