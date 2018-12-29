package com;

public class Check {

    // Check is String = Number ?
    public boolean isNum(String string) 
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
    public boolean isInt(String string) 
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
    public String NetFileName(String string) 
    {
        if (string.endsWith(".xlsx"))
            return string.replace(".xlsx", "");
        else
            return string;
    }

    public void usageErr() 
    {
        System.err.println("Insert java -jar xlsx2sqlite.jar </path/to/file.db> </path/to/file.xlsx> <optional : xlsx/page/name> <optional : db/table/name>");
        System.exit(-1);
    }

    public void nameErr() 
    {
        System.err.println("File has to be .xlsx file");
        System.exit(-1);
    }

    public void sheetnameErr() 
    {
        System.err.println("SheetName is Invalid");
        System.exit(-1);
    }

}
