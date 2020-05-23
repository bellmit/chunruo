package com.chunruo.core.vo;

import java.io.File;

/**
 * @Author: Will
 * @Date: 2018/12/12 10:16
 * @Description: 订单导入测试类
 */
public class EasyRequestVo extends EasySignRequestVo {

    private String compressed ="";

    private String transType="";
    
    private String orderType="";

    private String remark="";

    private File files;

    public String getCompressed() {
        return compressed;
    }

    public void setCompressed(String compressed) {
        this.compressed = compressed;
    }

    public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public File getFiles() {
        return files;
    }

    public void setFiles(File files) {
        this.files = files;
    }
}
