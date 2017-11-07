package com.github.dataanon.model

class Field<T: Any>(val name: String, val oldValue: T, var newValue: T = oldValue)