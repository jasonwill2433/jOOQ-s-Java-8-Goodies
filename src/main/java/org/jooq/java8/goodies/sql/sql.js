var someDatabaseFun = function() {
    var Properties = Java.type("java.util.Properties");
    var Driver = Java.type("org.h2.Driver");

    var driver = new Driver();
    var properties = new Properties();

    properties.setProperty("user", "sa");
    properties.setProperty("password", "");

    try {
        var conn = driver.connect("jdbc:h2:~/test", properties);

        print("");
        print("A plain JDBC example");
        print("--------------------");

        try {
            var stmt = conn.prepareStatement("select table_schema, table_name from information_schema.tables");
            var rs = stmt.executeQuery();

            while (rs.next()) {
                print(rs.getString("TABLE_SCHEMA") + "." + rs.getString("TABLE_NAME"))
            }
        }
        finally {
            if (rs)
                try {
                    rs.close();
                }
                catch(e) {}

            if (stmt)
                try {
                    stmt.close();
                }
                catch(e) {}
        }


        var DSL = Java.type("org.jooq.impl.DSL");

        print("");
        print("A plain SQL jOOQ example");
        print("------------------------");
        print(
            DSL.using(conn)
                .fetch("select table_schema, table_name from information_schema.tables")
        );

        var Tables = Java.type("org.jooq.db.h2.information_schema.Tables");
        var t = Tables.TABLES;
        var c = Tables.COLUMNS;
        var count = DSL.count;
        var row = DSL.row;

        print("");
        print("A SQL jOOQ example using generated code");
        print("---------------------------------------");
        print(
            DSL.using(conn)
               .select(t.TABLE_SCHEMA, t.TABLE_NAME, c.COLUMN_NAME)
               .from(t)
               .join(c)
               .on(row(t.TABLE_SCHEMA, t.TABLE_NAME)
                   .eq(c.TABLE_SCHEMA, c.TABLE_NAME))
               .orderBy(
                   t.TABLE_SCHEMA.asc(),
                   t.TABLE_NAME.asc(),
                   c.ORDINAL_POSITION.asc())
               .fetch()
        );

        print("");
        print("Another SQL jOOQ example using generated code and the Streams API");
        print("-----------------------------------------------------------------");
        DSL.using(conn)
            .select(t.TABLE_SCHEMA, t.TABLE_NAME, count().as("CNT"))
            .from(t)
            .join(c)
            .on(row(t.TABLE_SCHEMA, t.TABLE_NAME)
                .eq(c.TABLE_SCHEMA, c.TABLE_NAME))
            .groupBy(t.TABLE_SCHEMA, t.TABLE_NAME)
            .orderBy(
                t.TABLE_SCHEMA.asc(),
                t.TABLE_NAME.asc())
            .fetchMaps()
            .stream()
            .forEach(function (r) {
                print(r.TABLE_SCHEMA + '.' + r.TABLE_NAME + ' has ' + r.CNT + ' columns.');
            });

        DSL.using(conn)
            .select()
    }
    finally {
        if (conn)
            try {
                conn.close();
            }
            catch (e) {}
    }
}

someDatabaseFun();