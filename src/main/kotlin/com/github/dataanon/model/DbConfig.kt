package com.github.dataanon.model

import java.sql.Connection
import java.sql.DriverManager

class DbConfig(private val url: String, private val username: String, private val password: String) {

    fun connection(): java.sql.Connection =  java.sql.DriverManager.getConnection(url, username, password)
}