package com.example

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun main() { //Executing in main thread

//    println("Main program starts: ${Thread.currentThread().name}")

   /* thread {
        println("Fake work starts: ${Thread.currentThread().name}")
        Thread.sleep(1000)
        println("Fake work ends: ${Thread.currentThread().name}")

    }*/
    /*GlobalScope.launch {//Thread T1
        println("Fake work starts: ${Thread.currentThread().name}")
        delay(1000) // It is not blocking the thread, it is actually suspending the coroutine for 1 secs.
        println("Fake work ends: ${Thread.currentThread().name}") // It isn't necessary that it will run on T1 thread. It can run on any other thread
    }
//    Thread.sleep(2000)
    runBlocking {//Creates a coroutine that blocks the main thread
        delay(2000)
    }

    println("Main program ends: ${Thread.currentThread().name}")*/
    runBlocking {
        println("Program starts here:${Thread.currentThread().name}")
        GlobalScope.launch {
            println("Job 1 started here:${Thread.currentThread().name}")
            delay(1000)
            println("Job 1 ends here:${Thread.currentThread().name}")
        }
        delay(2000)
        println("Program ends here:${Thread.currentThread().name}")
    }

}