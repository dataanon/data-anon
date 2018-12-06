package com.github.dataanon

import com.github.dataanon.db.mongodb.MongoDbConfig
import com.github.dataanon.dsl.Whitelist
import com.github.dataanon.strategy.datetime.DateTimeRandomDelta
import com.github.dataanon.strategy.email.RandomEmail
import com.github.dataanon.strategy.name.RandomFirstName
import com.github.dataanon.strategy.name.RandomLastName
import com.github.dataanon.strategy.string.FixedString
import com.github.dataanon.strategy.string.StringTemplate
import java.time.Duration

fun main(args: Array<String>) {
    // Download sample data and import to test
    // wget https://raw.githubusercontent.com/sunitparekh/data-anonymization/master/sample-data/mongo/users.json
    // mongoimport -d testdb-source --drop -c users --jsonArray ./users.json

    val source = MongoDbConfig("mongodb://localhost:27017/testdb-source")
    val dest = MongoDbConfig("mongodb://localhost:27017/testdb-dest")

    Whitelist(source, dest)
            .table("users") {
                whitelist("_id", "failed_attempts", "updated_at")

                anonymize("date_of_birth").using(DateTimeRandomDelta(Duration.ofDays(365)))
                anonymize("user_id").using(StringTemplate("user-#{row_number}"))
                anonymize("email").using(RandomEmail())
                anonymize("password").using(FixedString("password"))
                anonymize("first_name").using(RandomFirstName())
                anonymize("last_name").using(RandomLastName())
                anonymize("password_reset_answer")
                anonymize("password_reset_question")
            }
            .execute()
}
