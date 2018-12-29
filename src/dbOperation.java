import java.sql.*;
import java.util.ArrayList;

class dbOperation {
    private Connection connect(String dbName) {
        // SQLite connection string
        String full_url = "jdbc:sqlite:" + dbName;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(full_url);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    void insertTable(String dbName, String tableName,
                     ArrayList<ArrayList<String>> excelContent, ArrayList<String> type) {
        // Delete table if exists
        String deletesql = "DROP TABLE IF EXISTS " + tableName + ";";

        // Create new table
        StringBuilder tableBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(\n");
        tableBuilder.append("id integer primary key,\n");

        for (int j = 0; j < type.size(); j++) {
            String temp = excelContent.get(0).get(j) + " " + type.get(j);
            if (j == type.size() - 1)
                temp += "\n";
            else
                temp += ",\n";
            tableBuilder.append(temp);
        }
        tableBuilder.append(");");

        StringBuilder tableInserter = new StringBuilder();

        for (int i = 1; i < excelContent.size(); i++) {
            ArrayList<String> strings = excelContent.get(i);

            StringBuilder temp = new StringBuilder("INSERT INTO ");
            temp.append(tableName);

            // Entry name
            temp.append(" (");

            ArrayList<String> names = excelContent.get(0);
            for (int j = 0; j < names.size(); j++) {
                temp.append(names.get(j));
                if (j != names.size() - 1)
                    temp.append(", ");
            }

            temp.append(")");
            temp.append(" VALUES (");

            for (int j = 0; j < strings.size(); j++) {
                String string = strings.get(j);

                if (!type.get(j).equals("int") && !(type.get(j).equals("real"))) {
                    // char
                    temp.append("'");
                    temp.append(string);
                    temp.append("'");
                }
                else {
                    // int or real
                    temp.append(string);
                }

                if (j != strings.size() - 1)
                    temp.append(", ");
            }

            temp.append(");\n");
            tableInserter.append(temp);
        }

        String createsql = tableBuilder.toString();
        String insertsql = tableInserter.toString();
//        System.out.println(createsql);
//        System.out.println(insertsql);

        try {
            Connection conn = this.connect(dbName);
            Statement stmt = conn.createStatement();
            stmt.execute(deletesql);
            stmt.execute(createsql);
            stmt.execute(insertsql);
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void printoutTable(String tableName, ArrayList<ArrayList<String>> excelContent, ArrayList<String> type) {
        // Print out table name
        System.out.println("Table name: " + tableName);
        // Print out line numbers
        System.out.println("Number of lines: " + (excelContent.size() - 1));

        System.out.println();
        System.out.printf("%-12s\t%-12s", "Field", "Type");
        System.out.println();

        // Print out table
        ArrayList<String> strings = excelContent.get(0);

        for (int i = 0; i < type.size(); i++) {
            System.out.printf("%-12s\t%-12s", strings.get(i), type.get(i));
            System.out.println();
        }
    }
}
