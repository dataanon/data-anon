package com.github.dataanon.strategy.list

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.strategy.AnonymizationStrategy
import com.github.dataanon.utils.FlatFileContentStore
import com.github.dataanon.utils.RandomSampling
import kotlin.reflect.KClass

open class PickFromFile<T: Any>(filePath: String) : AnonymizationStrategy<T>, RandomSampling {

    init {
        require(filePath.isNotBlank(), {"filePath can not be empty while using PickFromFile"})
    }

    private val values = FlatFileContentStore.getFileContentByPath(filePath)

    override fun anonymize(field: Field<T>, record: Record): T {
        return cast(sample(values), field.oldValue::class)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun cast(str: String, type: KClass<out T>): T = when(type){
        Int::class          -> str.toInt()
        Float::class        -> str.toFloat()
        Double::class       -> str.toDouble()
        Boolean::class      -> str.toBoolean()
        String::class       -> str
        else                -> throw  IllegalArgumentException("type $type not supported in PickFromFile")
    } as T
}