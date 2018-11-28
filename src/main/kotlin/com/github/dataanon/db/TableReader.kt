package com.github.dataanon.db

import com.github.dataanon.model.Record
import org.reactivestreams.Publisher

interface TableReader : Publisher<Record> {
    // TODO: rename this?
    fun totalNoOfRecords(): Int
}
