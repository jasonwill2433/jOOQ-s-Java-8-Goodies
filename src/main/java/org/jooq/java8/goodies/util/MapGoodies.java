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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Eder
 */
public class MapGoodies {

    public static void main(String[] args) throws Exception {
        System.out.println("\ncompute()");
        System.out.println("---------");
        compute();

        System.out.println("\nforEach()");
        System.out.println("---------");
        forEach();

        System.out.println("\ngetOrDefault()");
        System.out.println("------------");
        getOrDefault();

        System.out.println("\nmerge()");
        System.out.println("---------");
        merge();
    }

    private static void compute() {
        Map<String, Integer> map = map();

        System.out.println(map.compute("A", (k, v) -> v == null ? 42 : v + 41));
        System.out.println(map);

        System.out.println(map.compute("X", (k, v) -> v == null ? 42 : v + 41));
        System.out.println(map);
    }

    private static void forEach() {
        Map<String, Integer> map = map();

        map.forEach((k, v) -> System.out.println(k + "=" + v));
    }

    private static void getOrDefault() {
        Map<String, Integer> map = map();

        System.out.println(map.getOrDefault("X", 42));

        // Pay attention to nulls!
        map.put("X", null);
        try {
            System.out.println(map.getOrDefault("X", 21) + 21);
        }
        catch (NullPointerException nope) {
            nope.printStackTrace();
        }
    }

    private static void merge() {
        Map<String, Integer> map = map();

        map.put("X", null);
        System.out.println(map.merge("X", 1, (v1, v2) -> null));
        System.out.println(map);
    }

    private static Map<String, Integer> map() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        return map;
    }
}
