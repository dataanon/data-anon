package com.github.dataanon

import com.github.dataanon.strategies.AnonymizationStrategy
import com.github.dataanon.strategies.DefaultStringStrategy

class Column(val name: String) {
    var strategy: AnonymizationStrategy = DefaultStringStrategy()

    fun using(strategy: AnonymizationStrategy) {
        this.strategy = strategy
    }
}