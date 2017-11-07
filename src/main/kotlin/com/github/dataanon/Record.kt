package com.github.dataanon

class Record(private val fields: List<Field<Any>>, val rowNum: Int) {

    fun find(name: String): Field<Any> = fields.first {name.equals(it.name, true)}
}