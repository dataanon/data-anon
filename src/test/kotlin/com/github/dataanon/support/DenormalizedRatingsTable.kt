package com.github.dataanon.support

import com.github.dataanon.model.DbConfig
import java.sql.Connection
import java.sql.Timestamp
import java.time.LocalDateTime

class DenormalizedRatingsTable(dbConfig: DbConfig) {
    private var conn: Connection = dbConfig.connection()

    init {
        conn.createStatement().executeUpdate("DROP TABLE IF EXISTS DENORMALIZEDRATINGS")
        val createDenormalizedRatingsTable = "CREATE TABLE DENORMALISEDRATINGS( " +
            "MOVIE VARCHAR2(255), " +
            "USER VARCHAR2(255), " +
            "RATING INT, " +
            "CREATED_AT TIMESTAMP, " +
            "PRIMARY KEY(MOVIE, USER) )"
        conn.createStatement().executeUpdate(createDenormalizedRatingsTable)
    }

    fun insert(movie: String, user: String, rating: Int, createdAt: LocalDateTime): DenormalizedRatingsTable {
        val stmt = conn.prepareStatement("INSERT INTO DENORMALISEDRATINGS(MOVIE,USER,RATING,CREATED_AT) VALUES(?,?,?,?)")
        stmt.setString(1, movie)
        stmt.setString(2, user)
        stmt.setInt(3, rating)
        stmt.setTimestamp(4, Timestamp.valueOf(createdAt))
        stmt.executeUpdate()
        stmt.close()
        return this
    }

    fun findAll(): List<Map<String, Any>> {
        val records = mutableListOf<Map<String, Any>>()
        val rs = conn.createStatement().executeQuery("SELECT * FROM DENORMALISEDRATINGS")
        while (rs.next()) {
            val record = hashMapOf<String, Any>()
            record["MOVIE"] = rs.getString("MOVIE")
            record["USER"] = rs.getString("USER")
            record["RATING"] = rs.getInt("RATING")
            record["CREATED_AT"] = rs.getTimestamp("CREATED_AT").toLocalDateTime()
            records.add(record)
        }
        rs.close()
        return records
    }

    fun close() {
        conn.close()
    }
}