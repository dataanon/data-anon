package com.github.dataanon

import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.strategy.number.FixedDouble

/**
 * CREATE TABLE RATINGS_A(USERID INTEGER, MOVIEID INTEGER,RATING NUMERIC,TIMESTAMP BIGINT, PRIMARY KEY(USERID, MOVIEID))
 */
fun main(args: Array<String>) {
    val source = DbConfig("jdbc:postgresql://localhost:5432/movies", "sunitparekh", "")
    val dest = DbConfig("jdbc:postgresql://localhost:5432/moviesdest", "sunitparekh", "")

    Whitelist(source,dest)
            .table("RATINGS_A") {
                whitelist("MOVIEID","USERID","TIMESTAMP")
                anonymize("RATING").using(FixedDouble(4.3))
            }
            .execute(limit = 1_00_000)
}