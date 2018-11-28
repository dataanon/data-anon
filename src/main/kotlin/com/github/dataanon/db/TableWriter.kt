package com.github.dataanon.db

import com.github.dataanon.model.Column
import com.github.dataanon.model.Record
import com.github.dataanon.model.Table
import com.github.dataanon.utils.ProgressBarGenerator
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.SignalType

abstract class TableWriter(
        internal val table: Table,
        internal val progressBar: ProgressBarGenerator,
        private val onFinally: (() -> Unit)? = null)
    : BaseSubscriber<Record>() {

    internal val fields: List<Column> = table.allColumnObjects()

    override fun hookOnSubscribe(subscription: Subscription) {
        request(1)
    }

    override fun hookOnError(throwable: Throwable) {
        throw throwable
    }

    override fun hookFinally(type: SignalType) {
        onFinally?.invoke()
    }

}
