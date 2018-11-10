package com.github.dataanon.model

class Record(private val fields: List<Field<Any>>, val rowNum: Int) {

    fun find(name: String): Field<Any> = fields.first { name.equals(it.name, true) }
    fun find(column: Column): Field<Any> {
        val field = fields.first { column.name.equals(it.name, true) }
        return Field(name = field.name, oldValue = field.oldValue, newValue = field.newValue, isKey = column.isKey)
    }
}