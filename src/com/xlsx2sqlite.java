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

public class xlsx2sqlite {

    // Check is String = Number ?
    private static boolean isNum(String string) 
    {
        if ((string.charAt(0) > '9' || string.charAt(0) < '0')
                && string.charAt(0) != '-' && string.charAt(0) != '+')
            return false;
        for (int i = 1; i < string.length(); i++)
            if ((string.charAt(i) > '9' || string.charAt(i) < '0') && string.charAt(i) != '.')
                return false;
        return true;
    }

    // Check is String = Integer ?
    private static boolean isInt(String string) 
    {
        if (!isNum(string)) return false;
        int pos = -1;
        for (int i = 0; i < string.length(); i++) 
        {
            if (string.charAt(i) == '.') 
            {
                pos = i;
                break;
            }
        }

        if (pos != -1) 
        {
            string = string.substring(pos + 1);
            for (int i = 0; i < string.length(); i++) 
            {
                if (string.charAt(i) != '0')
                    return false;
            }
        }
        return true;
    }

    // Delete .xlsx
    private static String NetFileName(String string) 
    {
        if (string.endsWith(".xlsx"))
            return string.replace(".xlsx", "");
        else
            return string;
    }

    private static void usageErr() 
    {
        System.err.println("Insert java -jar xlsx2sqlite.jar </path/to/file.db> </path/to/file.xlsx> <optional : xlsx/page/name> <optional : db/table/name>");
        System.exit(-1);
    }

    private static void nameErr() 
    {
        System.err.println("File has to be .xlsx file");
        System.exit(-1);
    }

    private static void sheetnameErr() 
    {
        System.err.println("SheetName is Invalid");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException
    {
        //Parameters Check
        if (args.length < 2) usageErr();
        if (!args[1].endsWith(".xlsx"))nameErr();

        //Declarations
        DBEdit dbedit = new DBEdit();
        String DBName, FileName, SheetName, TableName;
        DBName = args[0];
        FileName = args[1];
        int LengthOfContent = 0;
        ArrayList<ArrayList<String>> ContentOfXlsx = new ArrayList<>();

        //When has at least three parameters, get SheetName
        if (args.length > 2 || args.length <= 4) SheetName = args[2];
        //If there is only two parameters, get first sheet
        else SheetName = null;

        //If has 3 parameters then TableName = SheetName
        if (args.length == 3) TableName = SheetName;
        //If has 4th parameter, then get TableName
        else if (args.length == 4) TableName = args[3];
         //When there is only two parameters, TableName is FileName
        else TableName = NetFileName(FileName);
            
        //Declare File and open xlsx file
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
        if (Sheet == null) sheetnameErr();
        
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
                else if (isInt(string)) continue;
                // Real entry
                else if (isNum(string)) 
                {
                    if (ColumnType.equals("int")) ColumnType = "real";
                    continue;
                }
                // String entry
                ColumnType = "char(" + LengthOfContent + ")";
            }
            TypeOfData.add(ColumnType);
        }
        // Insert xlsx file into table
        dbedit.insertTable(ContentOfXlsx, TypeOfData, DBName, TableName);
        // Print out
        dbedit.showTable(ContentOfXlsx, TypeOfData, TableName);
        // Close files
        WorkBook.close();
        FileInput.close();
    }
}
