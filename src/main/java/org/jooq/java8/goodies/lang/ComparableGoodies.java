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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lukas on 23.01.14.
 */
public class ComparableGoodies {

static class Person {
    final String firstName;
    final String lastName;

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

    public static void main(String[] args) {
        List<Person> people =
        Arrays.asList(
            new Person("Jane", "Henderson"),
            new Person("Michael", "White"),
            new Person("Henry", "Brighton"),
            new Person("Hannah", "Plowman"),
            new Person("William", "Henderson")
        );

        System.out.println("Apply Java-7-style sorting");
        System.out.println("--------------------------");
        people.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                int result = o1.lastName.compareTo(o2.lastName);

                if (result == 0)
                    result = o1.firstName.compareTo(o2.firstName);

                return result;
            }
        });
        people.forEach(System.out::println);

        System.out.println();
        System.out.println("Apply Java-8-style sorting");
        System.out.println("--------------------------");
        Comparator<Person> c = (p, o) ->
            p.lastName.compareTo(o.lastName);

        c = c.thenComparing((p, o) ->
            p.firstName.compareTo(o.firstName));

        people.sort(c);
        people.forEach(System.out::println);

        System.out.println();
        System.out.println("Apply Java-8-style sorting, more fluently");
        System.out.println("-----------------------------------------");
        people.sort(Utils.<Person>compare()
              .thenComparing((p, o) -> p.lastName.compareTo(o.lastName))
              .thenComparing((p, o) -> p.firstName.compareTo(o.firstName)));
        people.forEach(System.out::println);
        
        System.out.println();
        System.out.println("Apply Java-8-style sorting, using key extractors I");
        System.out.println("--------------------------------------------------");
        people.sort(Utils.<Person>compare()
              .thenComparing(p -> p.lastName)
              .thenComparing(p -> p.firstName));
        people.forEach(System.out::println);

        System.out.println();
        System.out.println("Apply Java-8-style sorting, using key extractors II");
        System.out.println("---------------------------------------------------");
        people.sort(
            Comparator.comparing((Person p) -> p.lastName)
                  .thenComparing(p -> p.firstName));
        people.forEach(System.out::println);
    }

    static class Utils {
        static <E> Comparator<E> compare() {
            return (e1, e2) -> 0;
        }
    }
}
