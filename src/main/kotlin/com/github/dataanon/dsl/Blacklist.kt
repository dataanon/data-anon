package com.github.dataanon.dsl

import com.github.dataanon.Anonymizer

class Blacklist(private val dbConfig: Map<String, String>) {

    fun table(name: String, init: Anonymizer.() -> Unit): Anonymizer {
        val anonymizer = Anonymizer(dbConfig, name)
        anonymizer.init()
        return anonymizer
    }
}