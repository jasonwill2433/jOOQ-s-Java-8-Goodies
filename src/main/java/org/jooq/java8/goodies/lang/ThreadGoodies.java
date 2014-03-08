/**
 * Copyright (c) 2011-2014, Data Geekery GmbH, contact@datageekery.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name "jOOU" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jooq.java8.goodies.lang;

import org.jooq.lambda.Unchecked;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Lukas on 02.03.14.
 */
public class ThreadGoodies {

    public static void main(String[] args) {
        jdk1_0();
        jdk5();
        jdk8();
    }

    public static int longOperation() {
        System.out.println("Running on thread #" + Thread.currentThread().getId());

        // [...]
        return 42;
    }

    public static int failingOperation() throws Exception {
        return 42;
    }

    /**
     * Improvements on inter-operation with JDK 1.0 code
     */
    private static void jdk1_0() {
        Thread[] threads = {

            // Pass a lambda to a thread
            new Thread(() -> {
                longOperation();
            }),

            // Pass a method reference to a thread
            new Thread(ThreadGoodies::longOperation),

            // Wrap lambdas throwing checked exceptions.
            new Thread(Unchecked.runnable(ThreadGoodies::failingOperation))
        };

        // Start all threads
        Arrays.stream(threads).forEach(Thread::start);

        // Join all threads
        Arrays.stream(threads).forEach(Unchecked.consumer(t -> t.join()));
    }

    /**
     * Improvements on inter-operation with JDK 5 code
     */
    private static void jdk5() {
        ExecutorService service = Executors.newFixedThreadPool(5);

        Future[] answers = {
            service.submit(() -> longOperation()),
            service.submit(ThreadGoodies::longOperation)
        };

        Arrays.stream(answers).forEach(Unchecked.consumer(
            f -> System.out.println(f.get())
        ));
    }

    /**
     * Improvements introduced in the JDK 8
     */
    private static void jdk8() {
        Arrays.stream(new int[]{1, 2, 3, 4, 5, 6})
              .parallel()
              .max()
              .ifPresent(System.out::println);
    }
}
