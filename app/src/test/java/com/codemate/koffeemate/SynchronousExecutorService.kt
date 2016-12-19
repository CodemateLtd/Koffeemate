package com.codemate.koffeemate

import java.util.concurrent.*

class SynchronousExecutorService : ExecutorService {
    override fun shutdown() {
    }

    override fun shutdownNow(): List<Runnable>? = null
    override fun isTerminated() = false
    override fun isShutdown() = false

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit) = false

    override fun <T> submit(task: Callable<T>): Future<T>? = null
    override fun <T> submit(task: Runnable, result: T): Future<T>? = null
    override fun submit(task: Runnable): Future<*>? = null

    @Throws(InterruptedException::class)
    override fun <T> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>>? = null

    @Throws(InterruptedException::class)
    override fun <T> invokeAll(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit): List<Future<T>>? = null

    @Throws(InterruptedException::class, ExecutionException::class)
    override fun <T> invokeAny(tasks: Collection<Callable<T>>): T? = null

    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    override fun <T> invokeAny(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit): T? = null

    override fun execute(command: Runnable) {
        command.run()
    }
}