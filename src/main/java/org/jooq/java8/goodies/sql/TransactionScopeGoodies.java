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

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.Connection;

import static java.sql.DriverManager.getConnection;

/**
 * @author Lukas Eder
 */
public class TransactionScopeGoodies {


public static void main(String[] args) throws Exception {
    Class.forName("org.h2.Driver");
    try (Connection connection = getConnection("jdbc:h2:~/test-scope-goodies", "sa", "")) {
        connection.setAutoCommit(false);
        TransactionRunner silent = new TransactionRunner(connection);

        silent.run(ctx -> {
            ctx.execute("drop table if exists person");
            ctx.execute("create table person(id integer, first_name varchar(50), last_name varchar(50), primary key(id))");
        });

        silent.run(ctx -> {
            ctx.execute("insert into person values(1, 'John', 'Smith');");
            ctx.execute("insert into person values(1, 'Steve', 'Adams');");
        });

        silent.run(ctx -> {
            ctx.execute("insert into person values(2, 'Jane', 'Miller');");
        });
        silent.run(ctx -> {
            ctx.execute("insert into person values(2, 'Anne', 'Roberts');");
        });

        silent.run(ctx -> {
            System.out.println(ctx.fetch("select * from person"));
        });
    }
}

    @FunctionalInterface
    interface Transactional {
        void run(DSLContext ctx) throws RuntimeException;
    }

    static class TransactionRunner {
        private final boolean silent;
        private final Connection connection;

        TransactionRunner(Connection connection) {
            this(connection, true);
        }

        TransactionRunner(Connection connection,
                          boolean silent) {
            this.connection = connection;
            this.silent = silent;
        }

        void run(Transactional tx) {
            // Initialise some jOOQ objects
            final DefaultConnectionProvider c = new DefaultConnectionProvider(connection);
            final Configuration configuration = new DefaultConfiguration().set(c).set(SQLDialect.H2);

            try {
                // Run the transaction and pass a jOOQ
                // DSLContext object to it
                tx.run(DSL.using(configuration));

                // If we get here, then commit the
                // transaction
                c.commit();
            }
            catch (RuntimeException e) {

                // Any exception will cause a rollback
                c.rollback();
                System.err.println(e.getMessage());

                // Eat exceptions in silent mode.
                if (!silent)
                    throw e;
            }
        }
    }
}
