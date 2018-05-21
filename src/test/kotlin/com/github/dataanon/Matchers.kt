package com.github.dataanon

import io.kotlintest.Matcher
import io.kotlintest.Result


interface Matchers {

    fun <T: Comparable<T>> beIn(range: ClosedRange<T>) = object : Matcher<T> {
        override fun test(value: T) =  Result(value in range, "$value not in the expected range $range")
    }

    fun <T> beIn(values: Collection<T>) = object : Matcher<T> {
        override fun test(value: T) =  Result(values.contains(value), "$value not in the collection $values")
    }

    fun notMatches(regex: Regex) = object : Matcher<String> {
        override fun test(value: String) =  Result(!value.contains(regex), "$value matches $regex")
    }

    fun contains(str: String) = object : Matcher<String> {
        override fun test(value: String) =  Result(value.contains(str), "$value does not contain $str")
    }
}
