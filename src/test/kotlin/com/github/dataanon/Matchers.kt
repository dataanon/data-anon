package com.github.dataanon

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult


interface Matchers {

    fun <T: Comparable<T>> beIn(range: ClosedRange<T>) = object : Matcher<T> {
        override fun test(value: T) =  MatcherResult(value in range, "$value not in the expected range $range","$value not in the expected range $range")
    }

    fun <T> beIn(values: Collection<T>) = object : Matcher<T> {
        override fun test(value: T) =  MatcherResult(values.contains(value), "$value not in the collection $values","$value not in the collection $values")
    }

    fun notMatches(regex: Regex) = object : Matcher<String> {
        override fun test(value: String) =  MatcherResult(!value.contains(regex), "$value matches $regex","$value matches $regex")
    }

    fun contains(str: String) = object : Matcher<String> {
        override fun test(value: String) =  MatcherResult(value.contains(str), "$value does not contain $str","$value does not contain $str")
    }
}
