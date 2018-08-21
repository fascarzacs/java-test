package support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * An representation of report data.
 */
public class Report
{
    private String title;
    private String subtitle;
    private String dates;
    
    private List<String> columnTitles = new ArrayList<String>();
    private List<String> columnProperties = new ArrayList<String>();
    private List<Object> rows = new ArrayList<Object>();
    
    public Report()
    {
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                            CSV Creation                           //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////

    public void writeCsv(String filename)
        throws FileNotFoundException
    {
        PrintStream printStream;
        if ("-".equals(filename))
            printStream = System.out;
        else
            printStream = new PrintStream(new File(filename));
        
        if (title != null)
            printStream.println("\"" + title + "\"");
        if (subtitle != null)
            printStream.println("\"" + subtitle + "\"");
        if (dates != null)
            printStream.println("\"" + dates + "\"");
        printStream.println();
        
        printRow(printStream, columnTitles);
        for (Object row : rows)
            printRow(printStream, row, columnProperties);
    }
    
    void printRow(PrintStream printStream, List<?> cells)
    {
        boolean first = true;
        for (Object cell : cells)
        {
            if (first)
                first = false;
            else
                printStream.print(",");
            printCell(printStream, cell.toString());
        }
        printStream.print("\n");
    }
    
    void printRow(PrintStream printStream, Object bean, List<String> properties)
    {
        boolean first = true;
        for (String property : properties)
        {
            if (first)
                first = false;
            else
                printStream.print(",");
            Object value;
            try
            {
                value = getBeanProperty(bean, property);
            }
            catch (Exception e)
            {
                value = "Error!";
            }
            printCell(printStream, value);
        }
        printStream.print("\n");
        
    }

    static void printCell(PrintStream printStream, Object value)
    {
        String string = value == null ? "" : value.toString();
        string = string.replace("\"", "\"\"");
        printStream.print("\"" + string + "\"");
    }
    
    public static Object getBeanProperty(Object bean, String property)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // Use reflection to get the associated property
        int index = -1;
        if (property.contains("["))
        {
            int i = property.indexOf('[');
            index = Integer.parseInt(property.substring(i + 1, i + 2));
            property = property.substring(0, i);
        }
        String methodName = property.substring(0, 1).toUpperCase()
                            + property.substring(1);
        Method method = bean.getClass().getMethod("get" + methodName);
        Object result = method.invoke(bean);
        if (index >= 0)
            return ((Object[])result)[index];
        else
            return result;
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                         Getters / Setters                         //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public String getDates()
    {
        return dates;
    }

    public void setDates(String dates)
    {
        this.dates = dates;
    }

    public void addColumn(String title, String property)
    {
        columnTitles.add(title);
        columnProperties.add(property);
    }

    public List<String> getColumnProperties()
    {
        return columnProperties;
    }

    public List<String> getColumnTitles()
    {
        return columnTitles;
    }

    public List<Object> getRows()
    {
        return rows;
    }

    public void addRow(Object row)
    {
        rows.add(row);
    }
}
