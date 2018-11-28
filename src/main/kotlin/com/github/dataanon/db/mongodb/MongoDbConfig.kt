package com.github.dataanon.db.mongodb

import com.github.dataanon.model.DbConfig
import com.mongodb.ConnectionString

class MongoDbConfig(uri: String) : DbConfig(uri) {

    internal val connectionString: ConnectionString = ConnectionString(uri)

}
