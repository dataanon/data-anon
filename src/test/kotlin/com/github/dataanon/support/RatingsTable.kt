package com.github.dataanon.support

import com.github.dataanon.model.DbConfig
import java.sql.Connection
import java.sql.JDBCType
import java.sql.Timestamp
import java.time.LocalDateTime

class RatingsTable(dbConfig: DbConfig) {
    private var conn: Connection = dbConfig.connection()

    init {
        conn.createStatement().executeUpdate("DROP TABLE IF EXISTS RATINGS")
        val createMovieTable = "CREATE TABLE RATINGS( " +
                "MOVIE_ID INT, " +
                "USER_ID INT, " +
                "RATING INT, " +
                "CREATED_AT TIMESTAMP, " +
                "PRIMARY KEY(MOVIE_ID, USER_ID) )"
        conn.createStatement().executeUpdate(createMovieTable)
    }

    fun insert(movieId: Int, userId: Int, rating: Int, createdAt: LocalDateTime) : RatingsTable {
        val stmt = conn.prepareStatement("INSERT INTO RATINGS(MOVIE_ID,USER_ID,RATING,CREATED_AT) VALUES(?,?,?,?)")
        stmt.setInt(1,movieId)
        stmt.setInt(2,userId)
        stmt.setInt(3,rating)
        stmt.setTimestamp(4,Timestamp.valueOf(createdAt))
        stmt.executeUpdate()
        stmt.close()
        return this
    }

    fun findAll(): List<Map<String, Any>> {
        val records = mutableListOf<Map<String,Any>>()
        val rs = conn.createStatement().executeQuery("SELECT * FROM RATINGS")
        while(rs.next()){
            val record = hashMapOf<String,Any>()
            record["MOVIE_ID"] = rs.getInt("MOVIE_ID")
            record["USER_ID"] = rs.getInt("USER_ID")
            record["RATING"] = rs.getInt("RATING")
            record["CREATED_AT"] = rs.getTimestamp("CREATED_AT").toLocalDateTime()
            records.add(record)
        }
        rs.close()
        return records
    }

    fun close(){
        conn.close()
    }
}