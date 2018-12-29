package com;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Main {
    public static void main(String[] args) throws IOException
    {
        long begintime = System.nanoTime();
    	Check chk = new Check();
        //Parameters Check
        if (args.length < 2) chk.usageErr();
        if (!args[1].endsWith(".xlsx"))chk.nameErr();

        //Declarations
        DBEdit dbedit = new DBEdit();
        String DBName, FileName, SheetName, TableName;
        DBName = args[0];
        FileName = args[1];
        int LengthOfContent = 0;
        ArrayList<ArrayList<String>> ContentOfXlsx = new ArrayList<>();

        //When has at least three parameters, get SheetName
        if (args.length == 3 || args.length == 4) SheetName = args[2];
        //If there is only two parameters, get first sheet
        else SheetName = null;

        //If has 3 parameters then TableName = SheetName
        if (args.length == 3) TableName = SheetName;
        //If has 4th parameter, then get TableName
        else if (args.length == 4) TableName = args[3];
         //When there is only two parameters, TableName is FileName
        else TableName = chk.NetFileName(FileName);
        //Declare File and open xlsx file
        
        // Print out Database and File name
        System.out.println("Database name: " + DBName+"\t File Name: "+ FileName);
       // Print out Sheet and table name
        System.out.println("Sheet name: "+SheetName+"\tTable name: " + TableName);
        
        File OpenFile = new File(FileName);
        FileInputStream FileInput = new FileInputStream(OpenFile);
        XSSFWorkbook WorkBook = new XSSFWorkbook(FileInput);
        //Get Sheet from xlsx File
        XSSFSheet Sheet;

        //Get Sheet with Sheet name provided
        if (SheetName != null) Sheet = WorkBook.getSheet(SheetName);
        //Get Default page
        else Sheet = WorkBook.getSheetAt(0);
        //Sheet with Name provided is not found
        if (Sheet == null) chk.sheetnameErr();
        
        //Get Every Row of the sheet
        for (Row row : Sheet) 
        {
            // Get every Cell of the Row
            Iterator<Cell> EveryCell = row.cellIterator();
            // Get Content of current Row
            ArrayList<String> TempContent = new ArrayList<>();
            while (EveryCell.hasNext()) 
            {
                Cell cell = EveryCell.next();
                TempContent.add(cell.toString());
                if (cell.toString().length() > LengthOfContent)
                    LengthOfContent = cell.toString().length();
            }
            // Add current row to ContentOfXlsx
            ContentOfXlsx.add(TempContent);
        }

        // Get current Type Of Data
        ArrayList<String> TypeOfData = new ArrayList<>();
        for (int j = 0; j < ContentOfXlsx.get(0).size(); j++) 
        {
            String ColumnType = "int";
            for (int i = 1; i < ContentOfXlsx.size(); i++) 
            {
                ArrayList<String> strings = ContentOfXlsx.get(i);
                // Each element of a column
                if (j >= strings.size()) strings.add("");                
                String string = strings.get(j);
                if (string.equals("")) continue;
                // Integer entry
                else if (chk.isInt(string)) continue;
                // Real entry
                else if (chk.isNum(string)) 
                {
                    if (ColumnType.equals("int")) ColumnType = "real";
                    continue;
                }
                // String entry
                ColumnType = "char(" + LengthOfContent + ")";
            }
            TypeOfData.add(ColumnType);
        }
       
        // Print out line numbers
        System.out.println("Inserting... Please be patient =)");
        // Insert xlsx file into table
        dbedit.createTable(ContentOfXlsx, TypeOfData, DBName, TableName);
        dbedit.insertTable(ContentOfXlsx, TypeOfData, DBName, TableName);
        // Print out
        dbedit.showSchema(ContentOfXlsx, TypeOfData, TableName);
        // Close files
        WorkBook.close();
        FileInput.close();
        long endtime = System.nanoTime();
        long costTime = (endtime - begintime)/1000000000;
        System.out.println("Time Consumed :"+costTime+" Seconds");
    }
}
