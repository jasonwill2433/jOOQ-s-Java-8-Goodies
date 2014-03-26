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
package org.jooq.java8.goodies.util;

import java.util.*;

/**
 * @author Lukas Eder
 */
public class OptionalGoodies {

    public static void main(String[] args) throws Exception {
        Optional<String> stringOrNot = Optional.of("123");

        // This String reference will never be null
        String alwaysAString = stringOrNot.orElse("");
        System.out.println(alwaysAString);

        // This Integer reference will be wrapped again
        Optional<Integer> integerOrNot = stringOrNot.map(Integer::parseInt);
        System.out.println(integerOrNot);

        // This int reference will never be null
        int alwaysAnInt = stringOrNot
                .map(s -> Integer.parseInt(s))
                .orElse(0);
        System.out.println(alwaysAnInt);

        // Streams also make heavy use of Optional types
        Optional<Integer> anyInteger =
        Arrays.asList(1, 2, 3)
              .stream()
              .filter(i -> i % 2 == 0)
              .findAny();
        anyInteger.ifPresent(System.out::println);

        // Primitive types
        OptionalInt anyInt =
        Arrays.stream(new int[] {1, 2, 3})
              .filter(i -> i % 2 == 0)
              .findAny();

        anyInt.ifPresent(System.out::println);
    }

    <T> void method() {
        Collection<Optional<? extends T>> source = new ArrayList<>();
        Collection<Optional<? super T>> target = new ArrayList<>();

        Optional<? extends T> s = source.iterator().next();

        // ... cannot put it into the target
        // target.add(s); // Nope
    }
}
