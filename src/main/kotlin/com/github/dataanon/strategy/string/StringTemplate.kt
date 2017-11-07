package com.github.dataanon.strategy.string

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.strategy.email.RandomEmail
import com.github.dataanon.strategy.name.RandomFirstName
import com.github.dataanon.strategy.name.RandomFullName
import com.github.dataanon.strategy.name.RandomLastName
import com.github.dataanon.strategy.string.SupportedStringTemplatePlaceholders.FIELD_VALUE
import com.github.dataanon.strategy.string.SupportedStringTemplatePlaceholders.ROW_NUMBER

class StringTemplate(private val template: String) : AnonymizationStrategy<String> {

    init {
        require(template.isNotBlank(), {"template can not be empty while using StringTemplate"})
    }

    override fun anonymize(field: Field<String>, record: Record): String = SupportedStringTemplatePlaceholders
                                                                    .evaluate(template, field, record)
                                                                    .replace("#{$ROW_NUMBER}",  record.rowNum.toString())
                                                                    .replace("#{$FIELD_VALUE}", field.oldValue)
}

object SupportedStringTemplatePlaceholders {
    val RANDOM_FN               = "random_fn"
    val RANDOM_LN               = "random_ln"
    val RANDOM_FULL_NAME        = "random_full_name"
    val RANDOM_EMAIL            = "random_email"
    val RANDOM_ALPHA            = "random_alpha"
    val RANDOM_ALPHA_NUMERIC    = "random_alpha_numeric"

    val ROW_NUMBER              = "row_number"
    val FIELD_VALUE             = "field_value"

    private val map = mapOf(RANDOM_FN               to StringTemplateEvaluationFunctions.randomFirstName,
                            RANDOM_LN               to StringTemplateEvaluationFunctions.randomLastName,
                            RANDOM_FULL_NAME        to StringTemplateEvaluationFunctions.randomFullName,
                            RANDOM_EMAIL            to StringTemplateEvaluationFunctions.randomEmail,
                            RANDOM_ALPHA            to StringTemplateEvaluationFunctions.randomAlpha,
                            RANDOM_ALPHA_NUMERIC    to StringTemplateEvaluationFunctions.randomAlphaNumeric
                          )

    fun evaluate(template: String, field: Field<String>, record: Record): String {
        var str = template
        for ((key, value) in map) str = str.replace("#{$key}", value(field, record))
        return str
    }
}

object StringTemplateEvaluationFunctions {

    val randomFirstName         = { field: Field<String>, record: Record -> RandomFirstName().anonymize(field, record) }
    val randomLastName          = { field: Field<String>, record: Record -> RandomLastName().anonymize(field, record) }
    val randomFullName          = { field: Field<String>, record: Record -> RandomFullName().anonymize(field, record) }
    val randomEmail             = { field: Field<String>, record: Record -> RandomEmail().anonymize(field, record) }
    val randomAlpha             = { field: Field<String>, record: Record -> RandomAlphabetic().anonymize(field, record) }
    val randomAlphaNumeric      = { field: Field<String>, record: Record -> RandomAlphaNumeric().anonymize(field, record) }
}