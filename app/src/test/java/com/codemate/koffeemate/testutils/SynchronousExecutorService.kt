/*
 * Copyright 2016 Codemate Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codemate.koffeemate.testutils

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