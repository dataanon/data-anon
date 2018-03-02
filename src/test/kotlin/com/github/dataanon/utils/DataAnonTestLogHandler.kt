package com.github.dataanon.utils

import java.util.logging.Handler
import java.util.logging.LogRecord

class DataAnonTestLogHandler: Handler() {
    companion object {
        val records = mutableListOf<LogRecord>()
    }

    override fun publish(record: LogRecord) {
        records.add(record)
    }

    override fun flush() {
    }

    override fun close() {
    }


}