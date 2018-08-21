package report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import support.Contract;
import support.ContractLineItem;
import support.Product;
import support.Report;

/**
 *  A report generator to create a report comparing
 *  contract prices from different distributors.
 */
public class ReportGenerator
{
    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                          Nested Classes                           //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////

    /** Data that make up this report.
     */
    public static class InputData
    {
        boolean compact;
        String title;
        String subtitle;
        String dates;
        
        /** Distributor names, sorted
         */
        private List<String> distributors = new ArrayList<String>();
        
        /** Staged rows
         */
        private Map<InputRowKey, InputRow> inputRowMap =
                    new HashMap<InputRowKey, InputRow>();
    }

    /** Contains the distributor name and a single price.
     */
    private class DistributorPrice
    {
        String distributorName;
        double price;

        DistributorPrice(String distributorName, double price)
        {
            this.price = price;
            this.distributorName = distributorName;
        }
    }

    /** A list of prices for a single product.
     */
    private class InputRow
    {
        String productName;
        List<DistributorPrice> prices = new ArrayList<DistributorPrice>();
    }

    /** A key to match identical products or identical contract lines (if contractName is not null).
     */
    private class InputRowKey
    {
        private String mfgName;
        private String productCode;
        private String contractName;

        public InputRowKey(String mfgName, String productCode, String contractName)
        {
            this.mfgName = mfgName;
            this.productCode = productCode;
            this.contractName = contractName;
        }
        
        public InputRowKey clone()
        {
            return new InputRowKey(mfgName, productCode, contractName);
        }
        
        public int hashCode()
        {
            int hash = mfgName.hashCode();
            hash = 29 * hash + productCode.hashCode();
            hash = 29 * hash + (contractName != null ? contractName.hashCode() : 0);
            return hash;
        }

        public boolean equals(Object obj)
        {
            if (obj == null || !(obj instanceof InputRowKey))
                return false;
            InputRowKey other = (InputRowKey) obj;
            if (!mfgName.equals(other.mfgName))
                return false;
            if (!productCode.equals(other.productCode))
                return false;
            if (contractName == null && other.contractName != null)
                return false;
            if (contractName != null && other.contractName == null)
                return false;
            if (contractName == null && other.contractName == null)
                return true;
            if (!contractName.equals(other.contractName))
                return false;
            return true;
        }
    }
    
    /** Utility class to obtain price orders
     */
    private class RowPriceIndex
    {
        ReportRow row;
        double price;
        int idx;

        RowPriceIndex(int idx, ReportRow row, double price)
        {
            this.row = row;
            this.price = price;
            this.idx = idx;
        }
    }


    ///////////////////////////////////////////////////////////////////////
    //                                                                   //
    //                         Report Generation                         //
    //                                                                   //
    ///////////////////////////////////////////////////////////////////////

    /**
     * Generates a Report object given a list of contracts.
     * @param contracts the contracts to search for distributor prices
     * @param compact whether to show multiple prices on each report row
     * @param customer the customer name for the report
     * @param dates the date range for the report
     * @return a Report object
     */
    public Report generateReport(List<Contract> contracts, boolean compact, String customer, String dates)
    {
        InputData inputData = new InputData();
        inputData.compact = compact;
        inputData.title = customer;
        if (compact)
            inputData.subtitle = "Price Comparison - compact";
        else
            inputData.subtitle = "Price Comparison";
        inputData.dates = dates;
        prepareInputData(contracts, inputData);
        return createReportFromInputData(inputData);
    }

    /**
     * Populate the InputData object from a list of contracts.
     */
    public void prepareInputData(List<Contract> contracts, InputData inputData)
    {
        for (Contract contract : contracts)
        {
            for (ContractLineItem lineItem : contract.getLineItems())
            {
                Product product = lineItem.getProduct();
                String manufacturer = product.getManufacturer();
                String productCode = product.productCode();
                String productName = product.getDescription();
                
                // Get all that is necessary to build report data.
                double price = lineItem.getPrice();
                String contractName = null;
                if (!inputData.compact)
                {
                    contractName = contract.getDescription() + " - " +
                            dateFormat.format(contract.getBeginDate()) + "-" +
                            dateFormat.format(contract.getEndDate());
                }
                String distributor = contract.getDistributor();
                InputRowKey key = new InputRowKey(
                        manufacturer, productCode, contractName);
                // Look up by key
                InputRow inputRow = inputData.inputRowMap.get(key);
                if (inputRow == null) 
                {
                    inputRow = new InputRow();
                    inputRow.productName = productName;
                    inputData.inputRowMap.put(key, inputRow);
                }
                DistributorPrice newDistributorPrice = new DistributorPrice(distributor, price);
                boolean priceExists = false;
                for (DistributorPrice distributorPrice : inputRow.prices)
                {
                    if (distributorPrice.distributorName.equals(newDistributorPrice.distributorName))
                        priceExists = true;
                }
                if (!priceExists)
                    inputRow.prices.add(newDistributorPrice);
            }
        }
    }

    /**
     * Generates a report from the InputData.
     * @param inputData an InputData containing report parameters and data.
     * @return a Report object
     */
    public Report createReportFromInputData(InputData inputData)
    {
        List<ReportRow> rows = generateRows(inputData.compact, inputData);
        
        Report reportModel = new Report();
        reportModel.setTitle(inputData.title);
        reportModel.setSubtitle(inputData.subtitle);
        reportModel.setDates(inputData.dates);
        
        reportModel.addColumn("Manufacturer", "manufacturerName");
        reportModel.addColumn("Product Name/Code", "productNameAndCode");
        if (!inputData.compact)
            reportModel.addColumn("Contract", "contractName");
        
        int asize = inputData.distributors.size();
        for (int i = 0; i < asize; i++)
        {
            reportModel.addColumn(inputData.distributors.get(i), "formattedPrices[" + i + "]");
        }
        for (ReportRow row : rows)
            reportModel.addRow(row);
        return reportModel;
    }

    /** Generates output rows from input data.
     * @param showContractName if true, each line will have only one distributor price.
     * @param inputData the already-populated input data
     * @return report construct
     */
    public List<ReportRow> generateRows(boolean compact, InputData inputData)
    {
        // Get a list of all possible distributors in the mix.
        SortedSet<String> distributors = new TreeSet<String>();
        for (InputRow prices : inputData.inputRowMap.values())
        {
            for (DistributorPrice v : prices.prices)
                distributors.add(v.distributorName);
        }
        inputData.distributors.addAll(distributors);
        int asize = inputData.distributors.size();
        
        // A map based on the same InputRowKey to determine price order.
        Map<InputRowKey,List<RowPriceIndex>> productPriceIndexMap = 
                new HashMap<InputRowKey,List<RowPriceIndex>>();
        
        // Build the row grid.
        List<ReportRow> outputRows = new ArrayList<ReportRow>();
        for (InputRowKey key : inputData.inputRowMap.keySet())
        {
            // Create and populate an output row.
            InputRow inputRow = inputData.inputRowMap.get(key);
            ReportRow reportRow = new ReportRow(asize);
            reportRow.setManufacturerName(key.mfgName);
            reportRow.setProductCode(key.productCode);
            reportRow.setProductName(inputRow.productName);
            reportRow.setContractName(key.contractName);
            
            // Get the RowPriceIndex list for this product.
            InputRowKey valueKey = key.clone();
            // Have to eliminate contract name to match all same-products.
            valueKey.contractName = null;
            List<RowPriceIndex> productPriceIndexList = productPriceIndexMap.get(valueKey);
            if (productPriceIndexList == null)
            {
                productPriceIndexList = new ArrayList<RowPriceIndex>();
                productPriceIndexMap.put(valueKey, productPriceIndexList);
            }
            
            // Store the distributor's price in the proper place in the ReportRow,
            // and add a record to this product's RowPriceIndex list.
            for (DistributorPrice distributorPrice : inputRow.prices)
            {
                int idx = inputData.distributors.indexOf(distributorPrice.distributorName);
                reportRow.setDistributorPrice(idx, distributorPrice.price);
                productPriceIndexList.add(new RowPriceIndex(idx, reportRow, distributorPrice.price));
            }
            outputRows.add(reportRow);
        }
        
        // For each product, as determined by the valueKey,
        // sort the prices and put the ordinal "(1), (2), etc"
        // into the related report rows.
        for (List<RowPriceIndex> rowPriceIndexList : productPriceIndexMap.values())
        {
            // Sort to obtain value order.
            Collections.sort(rowPriceIndexList, new Comparator<RowPriceIndex>()
                {
                    public int compare(RowPriceIndex rpi1, RowPriceIndex rpi2)
                    {
                        if (rpi1.price == rpi2.price)
                            return 0;
                        else if (rpi1.price > rpi2.price)
                            return 1;
                        else
                            return -1;
                    }
                });
            
            // Assign ordinals to the distributor prices in the report row.
            int i = 1;
            for (RowPriceIndex rpi : rowPriceIndexList)
            {
                rpi.row.setOrdinal(rpi.idx, i);
                i++;
            }
        }
        
        // Sort by manufacturer/product/contract name/value. 
        Collections.sort(outputRows);
        return outputRows;
    }
}
