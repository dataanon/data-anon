package com.github.dataanon

data class Field<T: Any>(val name: String, val oldValue: T, var newValue: T)