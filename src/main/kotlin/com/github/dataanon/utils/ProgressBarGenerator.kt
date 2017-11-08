package com.github.dataanon.utils

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarStyle

class ProgressBarGenerator(private val progressBarEnabled: Boolean = true, taskName: String, initialMaxFn: () -> Long) {

    private lateinit var pb: ProgressBar

    init {
        if (progressBarEnabled)
            pb = ProgressBar(taskName, initialMaxFn(), ProgressBarStyle.ASCII)
    }

    fun start() {
        if (progressBarEnabled) pb.start()
    }

    fun step() {
        if (progressBarEnabled) pb.step()
    }

    fun stop() {
        if (progressBarEnabled) pb.stop()
    }

}