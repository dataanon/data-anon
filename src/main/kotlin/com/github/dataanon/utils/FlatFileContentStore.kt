package com.github.dataanon.utils

import java.io.File

object FlatFileContentStore {

    private var fileContent: HashMap<String, List<String>> = HashMap()

    fun getFileContentByPath(path: String): List<String> = if ( !fileContent.containsKey(path) ) getFileContentPostRead(path) else fileContent[path] as List<String>


    private fun getFileContentPostRead(path: String): List<String> {
        val lineList = mutableListOf<String>()
        val resourceAsStream = this::class.java.getResourceAsStream(path) ?: File(path).inputStream()
        resourceAsStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }

        fileContent[path] = lineList
        return fileContent[path] as List<String>
    }
}