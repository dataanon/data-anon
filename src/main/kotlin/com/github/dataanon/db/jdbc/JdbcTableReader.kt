package com.github.dataanon.db.jdbc

import com.github.dataanon.db.TableReader
import com.github.dataanon.model.*
import org.reactivestreams.Subscriber
import reactor.core.publisher.Flux
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.logging.Logger

class JdbcTableReader(dbConfig: JdbcDbConfig, private val table: Table) : TableReader, Iterator<Record> {
    private val logger = Logger.getLogger(JdbcTableReader::class.java.name)

    private val conn = dbConfig.connection()
    private var rs: ResultSet
    private var index = 0

    init {
        val sql  = generateSelectQuery()
        val stmt = conn.createStatement()
        if (table.limit >= 1 ) stmt.maxRows = table.limit

        logger.info { "READ SQL: $sql" }
        rs       = stmt.executeQuery(sql)
    }

    // TODO: two db calls when table limit >= 1 and getTotalRecords() <= table.limit ?
    override fun totalNoOfRecords(): Int     = if (table.limit >= 1 && getTotalRecords() > table.limit) table.limit else getTotalRecords()

    override fun hasNext(): Boolean  = if (rs.next()) true else closeConnection()

    override fun next(): Record {
        index++
        return Record(table.allColumns().map { toField(it) }, index)
    }

    override fun subscribe(s: Subscriber<in Record>) {
        Flux.fromIterable(Iterable { this }).subscribe(s)
    }

    private fun toField(columnName: String)     = Field(columnName, columnValue(columnName))

    private fun columnValue(columnName: String): Any {
        val value = rs.getObject(columnName)
        return when (value) {
            is Date -> value.toLocalDate()
            is Timestamp -> value.toLocalDateTime()
            else -> value ?: NullValue
        }

    }

    private fun getTotalRecords(): Int {
        val rs = conn.createStatement().executeQuery(generateCountQuery())
        rs.next()
        val count = rs.getInt(1)
        rs.close()
        return count
    }

    private fun closeConnection(): Boolean {
        rs.close()
        conn.close()
        return false
    }

    private fun getWhereClause(): String {
        if (table.whereCondition is String && (table.whereCondition as String).isNotBlank()) {
            return " WHERE ${table.whereCondition} "
        }

        return ""
    }

    internal fun generateSelectQuery(): String {
        return "SELECT ${table.allColumns().joinToString(",")} FROM ${table.name} ${getWhereClause()}".trim()
    }

    internal fun generateCountQuery(): String {
        return "SELECT COUNT(1) FROM ${table.name} ${getWhereClause()}".trim()
    }
}
