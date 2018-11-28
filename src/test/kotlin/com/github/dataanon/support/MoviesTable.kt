package com.github.dataanon.support

import com.github.dataanon.db.jdbc.JdbcDbConfig
import java.sql.Connection
import java.sql.Date
import java.time.LocalDate

class MoviesTable(dbConfig: JdbcDbConfig) {
    private val conn: Connection = dbConfig.connection()

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

    fun insert(movieId: Int, title: String, genre: String?, releaseDate: LocalDate): MoviesTable {
        val stmt = conn.prepareStatement("INSERT INTO MOVIES(MOVIE_ID,TITLE,GENRE,RELEASE_DATE) VALUES(?,?,?,?)")
        stmt.setInt(1, movieId)
        stmt.setString(2, title)
        stmt.setString(3, genre)
        stmt.setDate(4, Date.valueOf(releaseDate))
        stmt.executeUpdate()
        stmt.close()
        return this
    }

    fun findAll(): List<Map<String, Any?>> {
        val records = mutableListOf<Map<String, Any?>>()
        val rs = conn.createStatement().executeQuery("SELECT * FROM MOVIES")
        while (rs.next()) {
            val record = hashMapOf<String, Any?>()
            record["MOVIE_ID"] = rs.getInt("MOVIE_ID")
            record["TITLE"] = rs.getString("TITLE")
            record["GENRE"] = rs.getString("GENRE")
            record["RELEASE_DATE"] = rs.getDate("RELEASE_DATE").toLocalDate()
            records.add(record)
        }
        rs.close()
        return records
    }

    fun close() {
        conn.close()
    }
}
