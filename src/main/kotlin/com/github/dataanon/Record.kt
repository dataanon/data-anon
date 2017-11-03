package com.github.dataanon

data class Record(val fields: List<Field>, val index: Int) {

    fun find(name: String): Field {
        return fields.first({f -> name.equals(f.name, true)})
    }
}