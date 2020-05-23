package com.chunruo.core.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

//XML文件中的根标识  
@XmlRootElement(name = "root")
public class PaymentInfoVo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 7614537531897838416L;
    
    private String orderNumber;
    
    private String mobile;
    
    private String realname;
    
    private String identityCard;
    
    private String notifyUrl;
    
    private PaymentInfoListVo productItems;
    
    public String getOrderNumber()
    {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber)
    {
        this.orderNumber = orderNumber;
    }
    
    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
    
    public String getRealname()
    {
        return realname;
    }

    public void setRealname(String realname)
    {
        this.realname = realname;
    }
    
    public String getIdentityCard()
    {
        return identityCard;
    }

    public void setIdentityCard(String identityCard)
    {
        this.identityCard = identityCard;
    }

    public String getNotifyUrl()
    {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl)
    {
        this.notifyUrl = notifyUrl;
    }

    public PaymentInfoListVo getProductItems()
    {
        return productItems;
    }

    public void setProductItems(PaymentInfoListVo productItems)
    {
        this.productItems = productItems;
    }
}
