package com.github.dataanon

import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.strategies.DefaultDoubleStrategy

/**
 * CREATE TABLE RATINGS_A(USERID INTEGER, MOVIEID INTEGER,RATING NUMERIC,TIMESTAMP BIGINT, PRIMARY KEY(USERID, MOVIEID))
 */
fun main(args: Array<String>) {
    val source = hashMapOf("url" to "jdbc:postgresql://localhost:5432/movies", "user" to "sunitparekh", "password" to ""
            , "limit" to 1000000L
    )
    val dest = hashMapOf("url" to "jdbc:postgresql://localhost:5432/moviesdest", "user" to "sunitparekh", "password" to "")


    Whitelist(source,dest)
            .table("RATINGS_A") {
                whitelist("MOVIEID","USERID","TIMESTAMP")
                anonymize("RATING").using(DefaultDoubleStrategy(4.3))
            }
            .execute()
}