package report;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *  A transient bean representing a row of product prices
 *  from multiple distributors.
 */
public class ReportRow implements Comparable<ReportRow>
{
	private static NumberFormat numberFormat = new DecimalFormat("0.00");
    private String manufacturerName;
    private String productName;
    private String productCode;
    private String contractName;
    private Double [] distributorPrices;
    private String [] formattedPrices; 
    
    
    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                            Constructor                            //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////
        
    /** Constructs instance with array size
     * @param arraySize array size equaling number of distributors.
     */
    public ReportRow(int arraySize)
    {
        distributorPrices = new Double[arraySize];
        formattedPrices = new String[arraySize];
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                         Getters / Setters                         //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////

    public void setDistributorPrice(int index, double price)
    {
    	distributorPrices[index] = price;
    }

    /**
     * Sets the order for a particular price value, and
     * generates a formatted value for reporting.
     * @param index the distributor index
     * @param ordinal the price order (1 is lowest price)
     */
    public void setOrdinal(int index, int ordinal)
    {
        Double v = distributorPrices[index];
        if (v != null)
        {
           formattedPrices[index] = "(" + ordinal + ") " +
                      numberFormat.format(v);
        }
    }
    
    public String [] getFormattedPrices()
    {
       return formattedPrices;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getManufacturerName()
    {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName)
    {
        this.manufacturerName = manufacturerName;
    }

    public String getContractName()
    {
        return contractName;
    }

    public void setContractName(String contractName)
    {
        this.contractName = contractName;
    }

    public String getProductNameAndCode()
    {
       return productName + " / " + productCode;
    }

    /** Compare by MFG/product name/contract name.
     * @param other other instance 
     */
    public int compareTo(ReportRow other)
    {
        int i = manufacturerName.compareTo(other.manufacturerName);
        if (i == 0)
        {
            i = productName.compareTo(other.productName);
            if (i == 0)
            {
                if (contractName != null)
                	i = contractName.compareTo(other.contractName);
            }
        }
        return i;
    }
}
