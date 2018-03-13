package com.github.dataanon.strategy.list

import com.github.dataanon.model.DbConfig
import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.RandomSampling

class PickFromDatabase<T: Any>(dbConfig: DbConfig, selectQuery: String) : AnonymizationStrategy<T>, RandomSampling {
    val values = mutableListOf<T>()

    init {
        val conn = dbConfig.connection()
        val stmt = conn.createStatement()
        val result = stmt.executeQuery(selectQuery)
        while (result.next()) values += result.getObject(1) as T
        stmt.close()
        conn.close()
        require(values.isNotEmpty(), {"values cannot be empty while using PickFromDatabase"})
    }
    override fun anonymize(field: Field<T>, record: Record): T = sample(values)
}