package com.github.dataanon.strategy.name

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.list.PickFromFile

class RandomFirstName(sourceFilePath: String = RandomFirstName::class.java.getResource("/data/first_names.dat").path) : AnonymizationStrategy<String> {

    init {
        require(sourceFilePath.isNotBlank(), {"sourceFilePath can not be empty while using RandomFirstName"})
    }

    private val pickFromFile = PickFromFile<String>(filePath = sourceFilePath)

    override fun anonymize(field: Field<String>, record: Record): String = pickFromFile.anonymize(field, record)
}