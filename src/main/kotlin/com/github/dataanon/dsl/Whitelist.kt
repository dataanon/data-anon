package com.github.dataanon.dsl

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.WhitelistTable

class Whitelist(private val sourceDbConfig: DbConfig, private val destDbConfig: DbConfig) : Strategy() {
    override fun sourceDbConfig(): DbConfig  = sourceDbConfig
    override fun destDbConfig(): DbConfig = destDbConfig

    fun table(tableName: String, init: WhitelistTable.() -> Unit): Whitelist {
        val table = WhitelistTable(tableName)
        table.init()
        tables.add(table)
        return this
    }

}