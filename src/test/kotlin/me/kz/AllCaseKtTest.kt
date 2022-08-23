package me.kz

import kotlinx.coroutines.channels.Channel
import org.openjdk.jmh.annotations.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(5)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
open class AllCaseKtTest {

    private val n = 99999

    @Benchmark
    fun actorRENDEZVOUS() {
        actor(n, Channel.RENDEZVOUS)
    }

    @Benchmark
    fun actorUNLIMITED() {
        actor(n, Channel.UNLIMITED)
    }

    @Benchmark
    fun executorSynchronousQueue() {
        executor(n, SynchronousQueue())
    }

    @Benchmark
    fun executorLinkedBlockingQueue() {
        executor(n, LinkedBlockingQueue())
    }
}

// -Xmx2g -Xms2g -Xmn1g

// n = 99999

// Benchmark                                  Mode  Cnt  Score    Error  Units
// AllCaseKtTest.actorRENDEZVOUS              avgt   25  0.192 ±  0.048   s/op
// AllCaseKtTest.actorUNLIMITED               avgt   25  0.037 ±  0.002   s/op
// AllCaseKtTest.executorLinkedBlockingQueue  avgt   25  0.029 ±  0.001   s/op
// AllCaseKtTest.executorSynchronousQueue     avgt   25  0.051 ±  0.001   s/op
