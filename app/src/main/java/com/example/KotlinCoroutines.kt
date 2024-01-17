package com.example

import android.graphics.DashPathEffect
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

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
//        println("Program starts here:${Thread.currentThread().name}")
//        GlobalScope.launch {
//            println("Job 1 started here:${Thread.currentThread().name}")
//            delay(1000)
//            println("Job 1 ends here:${Thread.currentThread().name}")
//        }
        /*val job:Job = launch {// coroutine is launched in it's main thread scope and that's why it inherits the scop of parent coroutine
            println("Job 1 started here:${Thread.currentThread().name}")
            delay(1000)
            println("Job 1 ends here:${Thread.currentThread().name}")
        }
        job.join()*/

        //Async coroutine builder
        /*val jobDeferred:Deferred<Int> = async {
            println("Job 1 started here:${Thread.currentThread().name}")
            delay(1000)
            println("Job 1 ends here:${Thread.currentThread().name}")
            15
        }
        jobDeferred.await()

        println("Program ends here:${Thread.currentThread().name}")
    }*/

        /*val job:Job = launch {
            try {
                for (i in 0..500) {
                    print(i)
//                Thread.sleep(50) // This is not cooperative because this function doesn't belong to kotlin coroutine package
                    delay(50) // This is cancellable and cooperative function because it belongs to kotlin coroutine package
                }
            } catch (e:Exception){
                print("\nException caught safely")
            } finally {
                //We shouldn't run suspending function in finally block.
                // But we can run it using withContext() function having parameter NonCancellable companion object
                withContext(NonCancellable) {
                    delay(1000)
                    print("\nClose resources in finally")
                }

            }

        }
        delay(200)
        job.cancel()
        job.join()
        println("Program ends here:${Thread.currentThread().name}")*/

        // By default there coroutines get executed sequntially
        println("Program starts here:${Thread.currentThread().name}")
        val time = measureTimeMillis {
            /*val msg1 = getSuspendingFuction1()
            val msg2 = getSuspendingFunction2()
            println("The message is: ${msg1 + msg2}")*/

            //This is concurrent execution of coroutines
            /*val msg1:Deferred<String> = async { getSuspendingFuction1() }
            val msg2:Deferred<String> = async { getSuspendingFunction2() }
            println("The message is ${msg1.await() + msg2.await()}")*/

            //Lazy execution of corutines - It will stop the execution of coroutine if there is no use of returned variables
            val msg1:Deferred<String> = async(start = CoroutineStart.LAZY) {
                getSuspendingFuction1()
            }
            val msg2:Deferred<String> = async(start = CoroutineStart.LAZY){
                getSuspendingFunction2()
            }
            println("The message is: ${msg1.await() + msg2.await()}")

        }
        println(time)
        println("Program ends here")

    }


}
suspend fun getSuspendingFuction1():String {
    delay(1000)
    return "First suspend function executed"
}
suspend fun getSuspendingFunction2():String {
    delay(1000)
    return "Second suspend function executed"
}