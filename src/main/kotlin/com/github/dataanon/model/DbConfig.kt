package com.github.dataanon.model

import java.sql.Connection
import java.sql.DriverManager.getConnection

open class DbConfig(private val url: String, private val username: String, private val password: String) {

    fun connection(): Connection =  getConnection(url, username, password)
}