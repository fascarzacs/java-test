package support;

public class Product
{
    private String manufacturer;
    private String productCode;
    private String description;
    
    public Product(String manufacturer, String productCode, String description)
    {
        this.manufacturer = manufacturer;
        this.productCode = productCode;
        this.description = description;
    }
    
    public String getManufacturer()
    {
        return manufacturer;
    }

    public String productCode()
    {
        return productCode;
    }

    public String getDescription()
    {
        return description;
    }
}
