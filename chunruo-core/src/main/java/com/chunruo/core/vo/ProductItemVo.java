package com.chunruo.core.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class ProductItemVo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 6747970538802002526L;
    
    private String amount;
    
    private String orderCount;
    
    private String productNumber;
    
    private String productName;
    
    private String skuNumber;
    
    private String skuName;
    
    private String barcode;
    
    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }
    
    public String getOrderCount()
    {
        return orderCount;
    }

    public void setOrderCount(String orderCount)
    {
        this.orderCount = orderCount;
    }
    
    public String getProductNumber()
    {
        return productNumber;
    }

    public void setProductNumber(String productNumber)
    {
        this.productNumber = productNumber;
    }
    
    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    
    public String getSkuNumber()
    {
        return skuNumber;
    }

    public void setSkuNumber(String skuNumber)
    {
        this.skuNumber = skuNumber;
    }
    
    public String getSkuName()
    {
        return skuName;
    }

    public void setSkuName(String skuName)
    {
        this.skuName = skuName;
    }
    
    public String getBarcode()
    {
        return barcode;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }
}
