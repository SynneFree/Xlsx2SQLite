package com;
import java.sql.*;
import java.util.ArrayList;

class DBEdit {
    void showTable(ArrayList<ArrayList<String>> ContentOfXlsx, ArrayList<String> TypeOfData, String TableName) 
    {
        // Print out table name
        System.out.println("TableName name: " + TableName);
        // Print out line numbers
        System.out.println("Number of lines: " + (ContentOfXlsx.size() - 1));

        System.out.println();
        System.out.printf("%-12s\t%-12s", "Field", "Type");
        System.out.println();

        // Print out table
        ArrayList<String> strings = ContentOfXlsx.get(0);

        for (int i = 0; i < TypeOfData.size(); i++) 
        {
            System.out.printf("%-12s\t%-12s", strings.get(i), TypeOfData.get(i));
            System.out.println();
        }
    }

    void insertTable(ArrayList<ArrayList<String>> ContentOfXlsx, ArrayList<String> TypeOfData, String dbName, String TableName) 
    {
        // Delete table if exists
        String deletesql = "DROP TABLE IF EXISTS " + TableName + ";";

        // Create new table
        StringBuilder tableBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS " + TableName + "(\n");
        tableBuilder.append("id integer primary key,\n");

        for (int j = 0; j < TypeOfData.size(); j++) 
        {
            String temp = ContentOfXlsx.get(0).get(j) + " " + TypeOfData.get(j);
            if (j == TypeOfData.size() - 1)
                temp += "\n";
            else
                temp += ",\n";
            tableBuilder.append(temp);
        }
        tableBuilder.append(");");

        StringBuilder tableInserter = new StringBuilder();

        for (int i = 1; i < ContentOfXlsx.size(); i++) 
        {
            ArrayList<String> strings = ContentOfXlsx.get(i);
            StringBuilder temp = new StringBuilder("INSERT INTO ");
            temp.append(TableName);

            // Entry name
            temp.append(" (");

            ArrayList<String> names = ContentOfXlsx.get(0);
            for (int j = 0; j < names.size(); j++) 
            {
                temp.append(names.get(j));
                if (j != names.size() - 1)
                    temp.append(", ");
            }

            temp.append(")");
            temp.append(" VALUES (");

            for (int j = 0; j < strings.size(); j++) 
            {
                String string = strings.get(j);

                if (!TypeOfData.get(j).equals("int") && !(TypeOfData.get(j).equals("real"))) 
                {
                    // char
                    temp.append("'");
                    temp.append(string);
                    temp.append("'");
                }
                else 
                {
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
        String sqlitePath = "jdbc:sqlite:" + dbName;
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(sqlitePath);
            Statement query = conn.createStatement();
            query.execute(deletesql);
            query.execute(createsql);
            query.execute(insertsql);
            conn.close();
        }
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
    }
}
