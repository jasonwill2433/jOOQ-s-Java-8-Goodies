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
package org.jooq.java8.goodies.sql;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.db.h2.information_schema.Tables;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.sql.DriverManager.getConnection;
import static java.util.stream.Collectors.*;
import static org.jooq.db.h2.information_schema.Tables.COLUMNS;

/**
 * @author Lukas Eder
 */
public class SQLGoodiesWithMapping {

    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");
        try (Connection c = getConnection("jdbc:h2:~/sql-goodies-with-mapping", "sa", "")) {
String sql =
    "select " +
        "table_name, " +
        "column_name, " +
        "type_name " +
    "from information_schema.columns " +
    "order by " +
        "table_catalog, " +
        "table_schema, " +
        "table_name, " +
        "ordinal_position";

            Result<Record> result =
            DSL.using(c)
               .fetch(sql);

            System.out.println();
            System.out.println("Unordered collection:");
            System.out.println("---------------------");
            result
                .stream()
                .collect(groupingBy(
                    r -> r.getValue("TABLE_NAME"),
                    mapping(
                        r -> r.getValue("COLUMN_NAME"),
                        toList()
                    )
                ))
                .forEach(
                    (table, columns) -> System.out.println(table + " : " + columns)
                );

            System.out.println();
            System.out.println("Ordered collection:");
            System.out.println("-------------------");
            result
                .stream()
                .collect(groupingBy(
                    r -> r.getValue("TABLE_NAME"),
                    LinkedHashMap::new,
                    mapping(
                        r -> r.getValue("COLUMN_NAME"),
                        toList()
                    )
                ))
                .forEach(
                    (table, columns) -> System.out.println(table + " : " + columns)
                );

            System.out.println();
            System.out.println("Generate DDL:");
            System.out.println("-------------");

            class Column {
                final String name;
                final String type;

                Column(String name, String type) {
                    this.name = name;
                    this.type = type;
                }
            }

            result
                .stream()
                .collect(groupingBy(
                    r -> r.getValue("TABLE_NAME"),
                    LinkedHashMap::new,
                    mapping(
                        r -> new Column(
                            r.getValue("COLUMN_NAME", String.class),
                            r.getValue("TYPE_NAME", String.class)
                        ),
                        toList()
                    )
                ))
                .forEach(
                    (table, columns) -> {
                        System.out.println("CREATE TABLE " + table + " (");
                        System.out.println(
                            columns.stream()
                                .map(col -> "  " + col.name + " " + col.type)
                                .collect(Collectors.joining(",\n"))
                        );

                        System.out.println(");");
                    }
                );

            System.out.println();
            System.out.println("Generate DDL with jOOQ:");
            System.out.println("-----------------------");

            DSL.using(c)
               .select(
                   COLUMNS.TABLE_NAME,
                   COLUMNS.COLUMN_NAME,
                   COLUMNS.TYPE_NAME
               )
               .from(COLUMNS)
               .orderBy(
                   COLUMNS.TABLE_CATALOG,
                   COLUMNS.TABLE_SCHEMA,
                   COLUMNS.TABLE_NAME,
                   COLUMNS.ORDINAL_POSITION
               )
               .fetch()
               .stream()
               .collect(groupingBy(
                   r -> r.getValue(COLUMNS.TABLE_NAME),
                   LinkedHashMap::new,
                   mapping(
                       r -> new Column(
                           r.getValue(COLUMNS.COLUMN_NAME),
                           r.getValue(COLUMNS.TYPE_NAME)
                       ),
                       toList()
                   )
               ))
               .forEach(
                   (table, columns) -> {
                       System.out.println("CREATE TABLE " + table + " (");
                       System.out.println(
                           columns.stream()
                               .map(col -> "  " + col.name + " " + col.type)
                               .collect(Collectors.joining(",\n"))
                       );

                       System.out.println(");");
                   }
               );
        }
    }
}
