package support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contract
{
    private String description;
    private Date beginDate;
    private Date endDate;
    private String distributor;
    
    private List<ContractLineItem> lineItems = new ArrayList<ContractLineItem>();
    
    public Contract(String description, Date beginDate, Date endDate, String distributor)
    {
        this.description = description;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.distributor = distributor;
    }
    
    public void addLineItem(ContractLineItem lineItem)
    {
        lineItem.setContractOwner(this);
        lineItems.add(lineItem);
    }
    
    public String getDescription()
    {
        return description;
    }

    public Date getBeginDate()
    {
        return beginDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public String getDistributor()
    {
        return distributor;
    }

    public List<ContractLineItem> getLineItems()
    {
        return lineItems;
    }
}
