package com.github.dataanon.dsl

import com.github.dataanon.model.BlacklistTable
import com.github.dataanon.model.DbConfig

class Blacklist(private val dbConfig: DbConfig): Strategy() {
    override fun sourceDbConfig(): DbConfig  = dbConfig
    override fun destDbConfig(): DbConfig = dbConfig

    fun table(tableName: String, primaryKey: List<String>, init: BlacklistTable.() -> Unit): Blacklist {
        val table = BlacklistTable(tableName, primaryKey)
        table.init()
        tables.add(table)
        return this
    }

}