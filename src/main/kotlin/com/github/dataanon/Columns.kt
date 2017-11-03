package com.github.dataanon

import com.github.dataanon.strategies.AnonymizationStrategy

class Columns: ArrayList<Column>() {

    fun names(): List<String> {
       return map { c -> c.name }
    }

    fun strategyFor(name: String): AnonymizationStrategy {
        return first { c -> name.equals(c.name, true) }.strategy
    }
}