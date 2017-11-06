package com.github.dataanon

data class Record(val fields: List<Field>, val rowNum: Int) {

    fun find(name: String): Field = fields.first {name.equals(it.name, true)}
}