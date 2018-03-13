package com.github.dataanon.strategy.name

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.list.PickFromFile

class RandomFullName(firstNameSourceFilePath: String = RandomFirstName::class.java.getResource("/data/first_names.dat").path,
                     lastNameSourceFilePath:  String = RandomFirstName::class.java.getResource("/data/last_names.dat").path) : AnonymizationStrategy<String> {

    init {
        require(firstNameSourceFilePath.isNotBlank(), {"firstNameSourceFilePath can not be empty while using RandomFullName"})
        require(lastNameSourceFilePath.isNotBlank(),  {"lastNameSourceFilePath can not be empty while using RandomFullName"})
    }

    private val pickFirstNames = PickFromFile<String>(filePath = firstNameSourceFilePath)
    private val pickLastNames  = PickFromFile<String>(filePath = lastNameSourceFilePath)

    override fun anonymize(field: Field<String>, record: Record): String = "${pickFirstNames.anonymize(field, record)} ${pickLastNames.anonymize(field, record)}"
}