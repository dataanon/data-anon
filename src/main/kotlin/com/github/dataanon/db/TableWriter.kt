package com.github.dataanon.db

import com.github.dataanon.model.Column
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber

abstract class TableWriter(internal val table: Table, internal val progressBar: ProgressBarGenerator) : BaseSubscriber<Record>() {

    internal val fields: List<Column> = table.allColumnObjects()

    override fun hookOnSubscribe(subscription: Subscription) {
        request(1)
    }

    override fun hookOnError(throwable: Throwable) {
        throw throwable
    }

}
