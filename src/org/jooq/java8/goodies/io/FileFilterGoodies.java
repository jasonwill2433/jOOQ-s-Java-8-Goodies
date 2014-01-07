/**
 * Copyright (c) 2011-2013, Lukas Eder, lukas.eder@gmail.com
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
package org.jooq.java8.goodies.io;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Java 8's lambda expressions greatly improve using the <code>java.io</code> package.
 *
 * @author Lukas Eder
 */
public class FileFilterGoodies {

    public static void main(String args[]) {
        listRecursive(new File("."));
    }

    /**
     * This method recursively lists all
     * .txt and .java files in a directory
     */
    private static void listRecursive(File dir) {
        Arrays.stream(dir.listFiles((f, n) ->
                     !n.startsWith(".")
                  &&
                     (f.isDirectory()
                  ||  n.endsWith(".txt")
                  ||  n.endsWith(".java"))
              ))
              .forEach(unchecked((file) -> {
                  System.out.println(
                      file.getCanonicalPath()
                          .substring(new File(".")
                          .getCanonicalPath()
                          .length()));

                      if (file.isDirectory()) {
                          listRecursive(file);
                      }
              }));
    }

    /**
     * This utility simply wraps a functional
     * interface that throws a checked exception
     * into a Java 8 Consumer
     */
    private static <T> Consumer<T>
    unchecked(CheckedConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    private interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }
}
