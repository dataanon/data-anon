package com.github.dataanon.utils

interface RandomSampling {

    fun <T: Any> sample(values: List<T>) = values[RandomUtils.generateRandomInt(to = values.size - 1)]
}