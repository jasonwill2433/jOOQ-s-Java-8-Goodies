package org.jooq.java8.goodies.sql;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;

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
