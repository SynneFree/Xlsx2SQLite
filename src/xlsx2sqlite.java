import java.io.File;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class xlsx2sqlite {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            // Not enough parameters
            System.out.println("Require more parameters!");
            return;
        }

        dbOperation dbop = new dbOperation();
        String databaseName, xlsxName, pageName, tableName;

        // Read in database name and xlsx file name
        databaseName = args[0];
        xlsxName = args[1];

        if (!xlsxName.endsWith(".xlsx"))
        {
            // Invalid file name
            System.out.println("Require a .xlsx file for the second parameter!");
            return;
        }

        // Get page name
        if (args.length == 3 || args.length == 4)
            pageName = args[2];
        else
            // The first page
            pageName = null;

        // Get table name
        if (args.length == 4)
            tableName = args[3];
        else if (args.length == 3)
            tableName = pageName;
        else
            tableName = DisposePostfix(xlsxName);

        // Open xlsx file
        File xlsxFile = new File(xlsxName);
        FileInputStream fis = new FileInputStream(xlsxFile);

        // Create XSSF workbook object
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // Get first sheet
        XSSFSheet sheet;
        if (pageName != null)
            sheet = workbook.getSheet(pageName);
        else
            // Default page
            sheet = workbook.getSheetAt(0);

        if (sheet == null) {
            System.out.println("Invalid page name!");
            return;
        }

        int maxContentLength = 0;
        ArrayList<ArrayList<String>> excelContent = new ArrayList<>();

        for (Row row : sheet) {
            // Iterate on cells for the current row
            Iterator<Cell> cellIterator = row.cellIterator();

            // Content of current row
            ArrayList<String> content = new ArrayList<>();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                content.add(cell.toString());

                // Update max content length
                if (cell.toString().length() > maxContentLength)
                    maxContentLength = cell.toString().length();
            }

            // Add current row to excel content
            excelContent.add(content);
        }

//        System.out.println(excelContent);

        // Get current type
        ArrayList<String> type = new ArrayList<>();

        for (int j = 0; j < excelContent.get(0).size(); j++) {
            // Each column
            String colType = "int";

            for (int i = 1; i < excelContent.size(); i++) {
                ArrayList<String> strings = excelContent.get(i);

                // Each element of a column
                if (j >= strings.size()) {
                    // Invalid index
                    strings.add("");
                }

                String string = strings.get(j);
                if (string.equals("")) {
                    continue;
                } else if (isInt(string)) {
                    // Integer entry
                    continue;
                } else if (isNum(string)) {
                    // Real entry
                    if (colType.equals("int"))
                        colType = "real";
                    continue;
                }

                // String entry
                colType = "char(" + maxContentLength + ")";
            }

            type.add(colType);
        }

//        System.out.println(type);

        // Insert xlsx file into table
        dbop.insertTable(databaseName, tableName, excelContent, type);

        // Print out
        dbop.printoutTable(tableName, excelContent, type);

        // Close file
        workbook.close();
        fis.close();
    }
    

    // Dispose of the post fix of xlsx file
    private static String DisposePostfix(String string) {
        if (string.endsWith(".xlsx"))
            return string.replace(".xlsx", "");
        else
            return string;
    }

    // Check whether a string is a number
    private static boolean isNum(String string) {
        if ((string.charAt(0) > '9' || string.charAt(0) < '0')
                && string.charAt(0) != '-' && string.charAt(0) != '+')
            return false;

        for (int i = 1; i < string.length(); i++)
            if ((string.charAt(i) > '9' || string.charAt(i) < '0') && string.charAt(i) != '.')
                return false;
        return true;
    }

    // Check whether a string is a integer
    private static boolean isInt(String string) {
        if (!isNum(string))
            return false;

        int pos = -1;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '.') {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            string = string.substring(pos + 1);
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) != '0')
                    return false;
            }
        }

        return true;
    }
}
