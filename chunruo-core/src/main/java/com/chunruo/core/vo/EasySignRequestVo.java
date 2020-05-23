package com.chunruo.core.vo;

/**
 * @Author: Will
 * @Date: 2018/12/11 17:49
 * @Description: 签名
 */
public abstract class EasySignRequestVo {

    private String supplierNo = "";  // 跨境商户号
    
    private String subSupplierNo = "";  // 跨境商户被代理商户

    private String sign = "";

    private String randomKey = "";

    private String origin ="";

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSubSupplierNo() {
		return subSupplierNo;
	}

	public void setSubSupplierNo(String subSupplierNo) {
		this.subSupplierNo = subSupplierNo;
	}

	public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
