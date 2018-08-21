package main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import report.ReportGenerator;
import support.Contract;
import support.ContractLineItem;
import support.Product;
import support.Report;

public class Main
{
    public static void main(String[] args)
        throws FileNotFoundException
    {
    	// Create a bunch of contracts.
        List<Contract> contracts = populateContracts();
       
        // Run old report - with one price per line and a contract column.
        ReportGenerator reportGenerator1 = new ReportGenerator();
        Report reportModel1 = reportGenerator1.generateReport(contracts, false, "Ikea USA", "01/26/2015 - 02/08/2015");
        reportModel1.writeCsv("price_comparison_full.csv");
        
        // Run the same report in compact form.
        ReportGenerator reportGenerator2 = new ReportGenerator();
        Report reportModel2 = reportGenerator2.generateReport(contracts, true, "Ikea USA", "01/26/2015 - 02/08/2015");
        reportModel2.writeCsv("price_comparison_compact.csv");
    }
    
    /**
     * Create products and contracts.
     * This simulates a simple database.
     */
    private static List<Contract> populateContracts()
    {
        List<Contract> contracts = new ArrayList<Contract>();
        
        Product blackberry = new Product("Georgia Fruit", "322-2295", "Blackberry");
        Product blueberry = new Product("Global Berry Farms", "652-1962", "Blueberry");
        Product grape = new Product("Leahy Associates", "56356", "Grape");
        Product papaya = new Product("Orchard Fresh", "0045", "Papaya");
        
        Contract contract;
        
        contract = new Contract("Davidson", getDate(1, 26), getDate(2, 8), "Davidson Foods");
        contract.addLineItem(new ContractLineItem(blackberry, 33.0));
        contract.addLineItem(new ContractLineItem(blueberry,  16.0));
        contract.addLineItem(new ContractLineItem(grape,       7.0));
        contract.addLineItem(new ContractLineItem(papaya,     26.0));
        contracts.add(contract);
        
        contract = new Contract("FoodShop #1", getDate(1, 26), getDate(2, 1), "Food Shop");
        contract.addLineItem(new ContractLineItem(blackberry, 32.0));
        contract.addLineItem(new ContractLineItem(blueberry,  15.0));
        contract.addLineItem(new ContractLineItem(grape,       9.0));
        contract.addLineItem(new ContractLineItem(papaya,     25.0));
        contracts.add(contract);
        
        contract = new Contract("FoodShop #2", getDate(2, 2), getDate(2, 8), "Food Shop");
        contract.addLineItem(new ContractLineItem(blackberry, 32.0));
        contract.addLineItem(new ContractLineItem(blueberry,  15.5));
        contract.addLineItem(new ContractLineItem(grape,       9.0));
        contract.addLineItem(new ContractLineItem(papaya,     25.0));
        contracts.add(contract);
        
        contract = new Contract("Genna", getDate(1, 26), getDate(2, 8), "Genna Foods");
        contract.addLineItem(new ContractLineItem(blackberry, 31.0));
        contract.addLineItem(new ContractLineItem(blueberry,  17.5));
        contract.addLineItem(new ContractLineItem(grape,       8.0));
        contract.addLineItem(new ContractLineItem(papaya,     27.0));
        contracts.add(contract);
        
        contract = new Contract("Thompson", getDate(1, 26), getDate(2, 8), "Thompson Foods");
        contract.addLineItem(new ContractLineItem(blackberry, 35.0));
        contract.addLineItem(new ContractLineItem(blueberry,  17.0));
        contract.addLineItem(new ContractLineItem(grape,       7.5));
        contract.addLineItem(new ContractLineItem(papaya,     24.0));
        contracts.add(contract);
        
        return contracts;
    }
    
    static Date getDate(int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, month, day);
        return calendar.getTime();
    }
}
