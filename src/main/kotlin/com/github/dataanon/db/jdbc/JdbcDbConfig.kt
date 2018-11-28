package com.github.dataanon.db.jdbc

import com.github.dataanon.model.DbConfig
import java.sql.Connection
import java.sql.DriverManager

class JdbcDbConfig(uri: String, private val username: String, private val password: String) : DbConfig(uri) {

    fun connection(): Connection = DriverManager.getConnection(uri, username, password)

}
