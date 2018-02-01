package com.github.dataanon.strategy.email

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.PickFromFile

class RandomEmail(private val host: String = "data-anonymization", private val tld: String = "com") : AnonymizationStrategy<String> {

    init {
        require(host.isNotBlank(), {"host can not be empty while using RandomEmail"})
        require(tld.isNotBlank(),  {"tld can not be empty while using RandomEmail"})
    }

    val pickFromFile = PickFromFile<String>(filePath = this::class.java.getResource("/data/first_names.dat").path)

    override  fun anonymize(field: Field<String>, record: Record): String = "${pickFromFile.anonymize(field, record)}${record.rowNum}@$host.$tld"
}