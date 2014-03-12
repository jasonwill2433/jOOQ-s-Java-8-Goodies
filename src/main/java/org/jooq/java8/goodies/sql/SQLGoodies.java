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

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.sql.DriverManager.getConnection;

/**
 * Created by Lukas on 23.01.14.
 */
public class SQLGoodies {
    static class Schema {
        final String schemaName;
        final boolean isDefault;

        Schema(String schemaName, boolean isDefault) {
            this.schemaName = schemaName;
            this.isDefault = isDefault;
        }

        @Override
        public String toString() {
            return "Schema{" +
                    "schemaName='" + schemaName + '\'' +
                    ", isDefault=" + isDefault +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {

        Class.forName("org.h2.Driver");
        try (Connection c = getConnection("jdbc:h2:~/test", "sa", "")) {
            String sql = "select schema_name, is_default from information_schema.schemata order by schema_name";

            System.out.println("Fetching data into a with JDBC / Java 7 syntax");
            System.out.println("----------------------------------------------");
            try (PreparedStatement stmt = c.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                        new Schema(rs.getString("SCHEMA_NAME"),
                                   rs.getBoolean("IS_DEFAULT"))
                    );
                }
            }

            System.out.println();
            System.out.println("Fetching data into a lambda expression with jOOQ");
            System.out.println("------------------------------------------------");
            DSL.using(c)
               .fetch(sql)
               .map(r -> new Schema(
                       r.getValue("SCHEMA_NAME", String.class),
                       r.getValue("IS_DEFAULT", boolean.class)
               ))
            // could also be written as
            // .into(Schema.class)
               .forEach(System.out::println);

            System.out.println();
            System.out.println("Fetching data into a lambda expression with Spring JDBC");
            System.out.println("-------------------------------------------------------");
            new JdbcTemplate(new SingleConnectionDataSource(c, true))
                .query(sql,
                    (rs, rowNum) -> new Schema(
                        rs.getString("SCHEMA_NAME"),
                        rs.getBoolean("IS_DEFAULT")
                    ))
                .forEach(System.out::println);

            System.out.println();
            System.out.println("Fetching data into a lambda expression with Apache DbUtils");
            System.out.println("-------------------------------------------------------");
            new QueryRunner()
                .query(c, sql,
                    new ArrayListHandler())
                .stream()
                .map(array -> new Schema(
                    (String) array[0],
                    (Boolean) array[1]
                ))
                .forEach(System.out::println);
        }
    }

    public static Transaction tx(Connection c) {
        return new Transaction(DSL.using(c));
    }

    static class Transaction implements AutoCloseable {
        final DSLContext ctx;

        Transaction(DSLContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void close() {

        }
    }
}
