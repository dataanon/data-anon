package com.github.dataanon

data class Record(val fields: List<Field>, val rowNum: Int) {

    fun find(name: String): Field {
        return fields.first({f -> name.equals(f.name, true)})
    }
}