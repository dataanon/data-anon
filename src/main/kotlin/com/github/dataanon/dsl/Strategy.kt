package com.github.dataanon.dsl

import com.github.dataanon.Anonymizer
import com.github.dataanon.Record

abstract class Strategy {
    protected lateinit var anonymizer: Anonymizer
    protected lateinit var tableName: String

    fun table(name: String, init: Anonymizer.() -> Unit): Strategy {
        this.tableName = name
        anonymizer = Anonymizer()
        anonymizer.init()
        return this
    }

    abstract fun execute()

    protected fun anonymize(record: Record): Record {
        anonymizer.columns.forEach { c ->
            val field = record.find(c.name)
            field.newValue = c.strategy.anonymize(field, record)
        }
        return record
    }

}