package me.kz

import kotlinx.coroutines.channels.Channel
import org.openjdk.jmh.annotations.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Fork(5)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
open class AllCaseKtTest {

    private val n = 99999
    private val bufferSize = n / 3

    @Benchmark
    fun actorRENDEZVOUS() {
        actor(n, Channel.RENDEZVOUS)
    }

    @Benchmark
    fun actorBUFFERED() {
        actor(n, bufferSize)
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
    fun executorArrayBlockingQueue() {
        executor(n, ArrayBlockingQueue(bufferSize))
    }

    @Benchmark
    fun executorLinkedBlockingQueue() {
        executor(n, LinkedBlockingQueue())
    }
}

// -Xmx4g -Xms4g -Xmn2g

// n = 99999
// bufferSize = n / 3

// Benchmark                                  Mode  Cnt  Score    Error  Units
// AllCaseKtTest.actorRENDEZVOUS              avgt   25  0.209 ±  0.048   s/op
// AllCaseKtTest.actorBUFFERED                avgt   25  0.031 ±  0.001   s/op
// AllCaseKtTest.actorUNLIMITED               avgt   25  0.035 ±  0.001   s/op
// AllCaseKtTest.executorSynchronousQueue     avgt   25  0.053 ±  0.001   s/op
// AllCaseKtTest.executorArrayBlockingQueue   avgt   25  0.016 ±  0.001   s/op
// AllCaseKtTest.executorLinkedBlockingQueue  avgt   25  0.029 ±  0.001   s/op
