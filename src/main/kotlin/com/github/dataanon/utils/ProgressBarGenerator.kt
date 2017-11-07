package com.github.dataanon.utils

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarStyle

class ProgressBarGenerator(private val progressBar: Boolean = true, taskName: String, initialMaxFn: () -> Long) {

    private lateinit var pb: ProgressBar

    init {
        if (progressBar)
            pb = ProgressBar(taskName, initialMaxFn(), ProgressBarStyle.ASCII)
    }

    fun start() {
        if ( isProgressBarEnabled() ) pb.start()
    }

    fun step() {
        if ( isProgressBarEnabled() ) pb.step()
    }

    fun stop() {
        if ( isProgressBarEnabled() ) pb.stop()
    }

    private fun isProgressBarEnabled() = progressBar
}