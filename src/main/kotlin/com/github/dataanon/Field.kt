package com.github.dataanon

class Field<T: Any>(val name: String, val oldValue: T, var newValue: T = oldValue)