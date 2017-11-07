package com.github.dataanon

import java.sql.Connection
import java.sql.DriverManager

class DbConfig(private val url: String, private val username: String, private val password: String) {

    fun connection(): Connection =  DriverManager.getConnection(url, username, password)
}