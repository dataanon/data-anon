package com.github.dataanon.db.mongodb

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.specs.FunSpec
import reactor.core.publisher.Hooks
import reactor.test.StepVerifier
import java.net.ServerSocket
import java.time.Duration
import de.flapdoodle.embed.mongo.MongodProcess

abstract class MongoSpec : FunSpec() {
    internal val localPort = ServerSocket(0).localPort
    private val mongodExecutable: MongodExecutable = MongodStarter.getDefaultInstance()
            .prepare(MongodConfigBuilder()
                    .version(Version.V3_6_5)
                    .net(Net(localPort, false))
                    .build())
    private var mongod: MongodProcess? = null

    override fun beforeSpec(description: Description, spec: Spec) {
        super.beforeSpec(description, spec)

        StepVerifier.setDefaultTimeout(Duration.ofSeconds(5))
        Hooks.onOperatorDebug()
        mongod = mongodExecutable.start()
    }

    override fun afterSpec(description: Description, spec: Spec) {
        super.afterSpec(description, spec)

        StepVerifier.resetDefaultTimeout()
        Hooks.resetOnOperatorDebug()
        mongod?.stop()
        mongodExecutable.stop()
    }
}
