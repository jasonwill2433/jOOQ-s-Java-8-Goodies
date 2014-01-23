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
