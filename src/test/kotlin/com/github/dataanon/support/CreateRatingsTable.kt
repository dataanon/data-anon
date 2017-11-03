package com.github.dataanon.support

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp

class CreateRatingsTable(dbConfig: HashMap<String, String>) {
    private var conn: Connection = DriverManager.getConnection(dbConfig["url"], dbConfig["user"], dbConfig["password"])

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

    fun insert(movieId: Int, userId: Int, rating: Int, createdAt: Timestamp) : CreateRatingsTable {
        val stmt = conn.prepareStatement("INSERT INTO RATINGS(MOVIE_ID,USER_ID,RATING,CREATED_AT) VALUES(?,?,?,?)")
        stmt.setInt(1,movieId)
        stmt.setInt(2,userId)
        stmt.setInt(3,rating)
        stmt.setTimestamp(4,createdAt)
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
            record["CREATED_AT"] = rs.getTimestamp("CREATED_AT")
            records.add(record)
        }
        rs.close()
        return records
    }

    fun close(){
        conn.close()
    }

}