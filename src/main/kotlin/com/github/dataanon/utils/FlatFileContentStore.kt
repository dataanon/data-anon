package com.github.dataanon.utils

import java.io.File

object FlatFileContentStore {

    private var fileContent: HashMap<String, List<String>> = File(this::class.java.getResource("/data/").path)
                                                            .walk()
                                                            .filter { it.extension.equals("dat") }
                                                            .associateByTo (HashMap(), { it.path }, { it.readLines() } )

    fun getFileContentByPath(path: String): List<String> = if ( !fileContent.containsKey(path) ) getFileContentPostRead(path) else fileContent[path] as List<String>


    private fun getFileContentPostRead(path: String): List<String> {
        fileContent[path] = File(path).readLines()
        return fileContent[path] as List<String>
    }
}