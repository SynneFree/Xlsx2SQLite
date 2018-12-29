package com;
import java.sql.*;
import java.util.ArrayList;

class DBEdit {
    //Execute SQLite Query
    void RunQuery(String DBName, String Statement){
        String DBPath = "jdbc:sqlite:" + DBName;
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(DBPath);
            Statement Query = conn.createStatement();
            Query.execute(Statement);
            conn.close();
        }
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    //Show Table Schema
    void showSchema(ArrayList<ArrayList<String>> ContentOfXlsx, ArrayList<String> TypeOfData, String TableName) 
    {
        // Print out table name
        System.out.println("TableName name: " + TableName);
        // Print out line numbers
        System.out.println("Number of lines: " + (ContentOfXlsx.size() - 2));
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
    void createTable(ArrayList<ArrayList<String>> ContentOfXlsx, ArrayList<String> TypeOfData, String DBName, String TableName)
    {
        // Delete table if exists
        String DropTableIfExist = "DROP TABLE IF EXISTS " + TableName + ";";

        // Create new table
        StringBuilder CreateTable = new StringBuilder("CREATE TABLE IF NOT EXISTS " + TableName + "(\n");
        CreateTable.append("NoOfLines int primary key,\n");

        for (int i = 0; i < TypeOfData.size(); i++) 
        {
            String temp = ContentOfXlsx.get(0).get(i) + " " + TypeOfData.get(i);
            if (i == TypeOfData.size() - 1)
                temp += "\n";
            else
                temp += ",\n";
            CreateTable.append(temp);
        }
        CreateTable.append(");");
        String SQLCreateTable = CreateTable.toString();
        RunQuery(DBName,DropTableIfExist);
        RunQuery(DBName,SQLCreateTable);
    }

    void insertTable(ArrayList<ArrayList<String>> ContentOfXlsx, ArrayList<String> TypeOfData, String DBName, String TableName) 
    {
        for (int i = 1; i < ContentOfXlsx.size(); i++) 
        {
        	StringBuilder InsertTable = new StringBuilder();
            ArrayList<String> strings = ContentOfXlsx.get(i);
            StringBuilder temp = new StringBuilder("INSERT INTO ");
            temp.append(TableName);
            //Start Bracket for columns 
            temp.append(" (");

            //Append Columns Names to the query
            temp.append("NoOfLines, ");
            ArrayList<String> names = ContentOfXlsx.get(0);
            for (int j = 0; j < names.size(); j++) 
            {
                temp.append(names.get(j));
                if (j != names.size() - 1)
                    temp.append(", ");
            }

            //End Bracket for colums
            temp.append(")");
            temp.append(" VALUES (");
            //No of Lines
            temp.append(i);
            temp.append(", ");
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
            temp.append(");");
            InsertTable.append(temp);
            String SQLInsertTable = InsertTable.toString();
            RunQuery(DBName,SQLInsertTable);
        }
    }
}
