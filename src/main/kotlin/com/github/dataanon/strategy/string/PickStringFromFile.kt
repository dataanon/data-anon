package com.github.dataanon.strategy.string

import com.github.dataanon.strategy.list.PickFromFile

class PickStringFromFile(filePath: String) : PickFromFile<String>(filePath)