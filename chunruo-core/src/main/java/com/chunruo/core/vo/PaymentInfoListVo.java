package com.chunruo.core.vo;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.chunruo.core.vo.ProductItemVo;

//XML文件中的根标识  
@XmlRootElement(name = "productItems")
public class PaymentInfoListVo implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 7197162245977517299L;
    
    private List<ProductItemVo> productItems;
    

    @XmlElement(name = "productItem")
    public List<ProductItemVo> getProductItems()
    {
        return productItems;
    }

    public void setProductItems(List<ProductItemVo> productItems)
    {
        this.productItems = productItems;
    }
}
