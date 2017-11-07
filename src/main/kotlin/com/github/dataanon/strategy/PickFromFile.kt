package com.github.dataanon.strategy

import com.github.dataanon.model.Field
import com.github.dataanon.model.Record
import com.github.dataanon.utils.FlatFileContentStore
import kotlin.reflect.KClass

open class PickFromFile<T: Any>(filePath: String) : com.github.dataanon.strategy.AnonymizationStrategy<T>, com.github.dataanon.utils.RandomSampling {

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