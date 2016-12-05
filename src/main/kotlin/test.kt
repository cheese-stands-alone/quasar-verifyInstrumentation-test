package com.test

import co.paralleluniverse.actors.ActorRef
import co.paralleluniverse.actors.ActorRegistry
import co.paralleluniverse.actors.LocalActor
import co.paralleluniverse.fibers.Suspendable
import co.paralleluniverse.kotlin.Actor
import co.paralleluniverse.kotlin.register
import co.paralleluniverse.kotlin.spawn

/**
 * Created by RJ on 12/5/2016.
 */

fun main(args: Array<String>) {
    val pong = spawn(register("pong", Pong()))
    val ping = spawn(Ping())
    LocalActor.join(pong)
    LocalActor.join(ping)
}

data class Msg(val txt: String, val from: ActorRef<Any?>)

class Ping : Actor() {
    @Suspendable override fun doRun() {
        val pong: ActorRef<Any> = ActorRegistry.getActor("pong")
        pong.send(Msg("ping", ref))
        (1..10).forEach {
            val msg = receive()
            when (msg) {
                is Msg ->  {
                    println("Ping received pong")
                    msg.from.send(Msg("ping", ref))
                }
            }
        }
        pong.send("finished")
        println("Ping exiting")
    }
}

class Pong : Actor() {
    @Suspendable override fun doRun() {
        while (true) {
            val msg = receive()
            when (msg) {
                is Msg -> {
                    if (msg.txt == "ping")
                        msg.from.send(Msg("pong", ref))
                }
                "finished" -> {
                    println("Pong received 'finished', exiting")
                    return
                }
            }
        }
    }
}
