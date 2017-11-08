package com.github.dataanon.dsl

import com.github.dataanon.model.Table

abstract class Strategy {
    protected var tables = mutableListOf<Table>()

    abstract fun execute(limit: Long = -1, progressBar: Boolean = true)
}