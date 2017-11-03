package com.github.dataanon.support

import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager

class MoviesTable(dbConfig: HashMap<String, String>) {
    private var conn: Connection = DriverManager.getConnection(dbConfig["url"], dbConfig["user"], dbConfig["password"])

    init {
            conn.createStatement().executeUpdate("DROP TABLE IF EXISTS MOVIES")
            val createMovieTable = "CREATE TABLE MOVIES( " +
                    "MOVIE_ID INT, " +
                    "TITLE VARCHAR2(255), " +
                    "GENRE VARCHAR2(255), " +
                    "RELEASE_DATE DATE, " +
                    "PRIMARY KEY(MOVIE_ID) )"
            conn.createStatement().executeUpdate(createMovieTable)
    }

    fun insert(movieId: Int, title: String, genre: String, releaseDate: Date) : MoviesTable {
        val stmt = conn.prepareStatement("INSERT INTO MOVIES(MOVIE_ID,TITLE,GENRE,RELEASE_DATE) VALUES(?,?,?,?)")
        stmt.setInt(1,movieId)
        stmt.setString(2,title)
        stmt.setString(3, genre)
        stmt.setDate(4, releaseDate)
        stmt.executeUpdate()
        stmt.close()
        return this
    }

    fun findAll(): List<Map<String, Any>> {
        val records = mutableListOf<Map<String,Any>>()
        val rs = conn.createStatement().executeQuery("SELECT * FROM MOVIES")
        while(rs.next()){
            val record = hashMapOf<String,Any>()
            record["MOVIE_ID"] = rs.getInt("MOVIE_ID")
            record["TITLE"] = rs.getString("TITLE")
            record["GENRE"] = rs.getString("GENRE")
            record["RELEASE_DATE"] = rs.getDate("RELEASE_DATE")
            records.add(record)
        }
        rs.close()
        return records
    }

    fun close(){
        conn.close()
    }

}