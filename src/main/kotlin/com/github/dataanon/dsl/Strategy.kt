package com.github.dataanon.dsl

import com.github.dataanon.Table
import com.github.dataanon.Record

abstract class Strategy {
    protected lateinit var table: Table

    fun table(tableName: String, init: Table.() -> Unit): Strategy {
        table = Table(tableName)
        table.init()
        return this
    }

    abstract fun execute()

    protected fun anonymize(record: Record): Record {
        table.columnsToBeAnonymized.forEach { c ->
            val field = record.find(c.name)
            field.newValue = c.strategy.anonymize(field, record)
        }
        return record
    }

}