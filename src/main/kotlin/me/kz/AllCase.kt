package me.kz

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
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
            val result = queue.take().invoke(i)
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
            queue.put(inc)
        }
    }

    produceDec.execute {
        val dec: Message = { it - 1 }
        repeat(n) {
            queue.put(dec)
        }
    }

    val incEnd = produceInc.submit { }
    val decEnd = produceDec.submit { }

    incEnd.get()
    decEnd.get()

    queue.put { POISON_PILL }

    consumer.submit { }.get()

    produceInc.shutdown()
    produceDec.shutdown()
    consumer.shutdown()
}

@OptIn(ObsoleteCoroutinesApi::class)
fun actor(n: Int, capacity: Int): Unit = runBlocking {
    val actor: SendChannel<Message> = actor(Dispatchers.IO, capacity = capacity) {
        var i = 0
        while (true) {
            val result = receive().invoke(i)
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
            actor.send(inc)
        }
    }

    val produceDec = launch(Dispatchers.IO) {
        launch {
            val dec: Message = { it - 1 }
            repeat(n) {
                actor.send(dec)
            }
        }
    }

    produceInc.join()
    produceDec.join()

    actor.send { POISON_PILL }

}
