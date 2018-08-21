package support;

public class ContractLineItem
{
    private Product product;
    private double price;
    private Contract contractOwner;
    
    public ContractLineItem(Product product, double price)
    {
        this.product = product;
        this.price = price;
    }
    
    public Contract getContractOwner()
    {
        return contractOwner;
    }

    public void setContractOwner(Contract contractOwner)
    {
        this.contractOwner = contractOwner;
    }

    public Product getProduct()
    {
        return product;
    }

    public double getPrice()
    {
        return price;
    }
}
