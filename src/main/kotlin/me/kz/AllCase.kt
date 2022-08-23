package me.kz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors

typealias Message = (Int) -> Int

const val POISON_PILL = Int.MIN_VALUE

fun executor(n: Int, queue: BlockingQueue<Message>) {
    val consumer = Executors.newSingleThreadExecutor()
    val produceInc = Executors.newSingleThreadExecutor()
    val produceDec = Executors.newSingleThreadExecutor()

    consumer.execute {
        var i = 0
        while (true) {
            val result = queue.take().invoke(i) // blocking take
            if (result == POISON_PILL) {
                break
            } else {
                i = result
            }
        }
    }

    produceInc.execute {
        val inc: Message = { it + 1 }
        repeat(n) {
            queue.put(inc) // blocking put
        }
    }

    produceDec.execute {
        val dec: Message = { it - 1 }
        repeat(n) {
            queue.put(dec) // blocking put
        }
    }

    val incEnd = produceInc.submit { }
    val decEnd = produceDec.submit { }

    incEnd.get() // blocking wait
    decEnd.get() // blocking wait

    queue.put { POISON_PILL }

    consumer.submit { }.get() // blocking wait

    produceInc.shutdown()
    produceDec.shutdown()
    consumer.shutdown()
}

fun channel(n: Int, channel: Channel<Message>): Unit = runBlocking {
    val consumer = launch(Dispatchers.IO) {
        var i = 0
        while (true) {
            val result = channel.receive().invoke(i) // suspend receive
            if (result == POISON_PILL) {
                break
            } else {
                i = result
            }
        }
    }

    val produceInc = launch(Dispatchers.IO) {
        val inc: Message = { it + 1 }
        repeat(n) {
            channel.send(inc) // suspend send
        }
    }

    val produceDec = launch(Dispatchers.IO) {
        launch {
            val dec: Message = { it - 1 }
            repeat(n) {
                channel.send(dec) // suspend send
            }
        }
    }

    produceInc.join() // suspend wait
    produceDec.join() // suspend wait

    channel.send { POISON_PILL }

    consumer.join() // suspend wait
}

@OptIn(ObsoleteCoroutinesApi::class)
fun actor(n: Int, capacity: Int): Unit = runBlocking {
    val actor: SendChannel<Message> = actor(Dispatchers.IO, capacity = capacity) {
        var i = 0
        while (true) {
            val result = receive().invoke(i) // suspend receive
            if (result == POISON_PILL) {
                break
            } else {
                i = result
            }
        }
    }

    val produceInc = launch(Dispatchers.IO) {
        val inc: Message = { it + 1 }
        repeat(n) {
            actor.send(inc) // suspend send
        }
    }

    val produceDec = launch(Dispatchers.IO) {
        launch {
            val dec: Message = { it - 1 }
            repeat(n) {
                actor.send(dec) // suspend send
            }
        }
    }

    produceInc.join() // suspend wait
    produceDec.join() // suspend wait

    actor.send { POISON_PILL }

}
