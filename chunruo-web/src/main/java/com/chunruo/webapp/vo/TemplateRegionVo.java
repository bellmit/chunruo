package com.chunruo.webapp.vo;

import java.util.ArrayList;
import java.util.List;
import com.chunruo.core.util.StringUtil;

public class TemplateRegionVo {
	private List<Long> areaIdList = new ArrayList<Long> ();	// 地区列表
	private int areaId;              //配送区域
	private String area;        	//地区
	private Double firstWeigth;	//首重
	private Double firstPrice;		//运费
	private Double afterWeigth;	//续重
	private Double afterPrice;		//续费
	private Double packageWeigth;  //包材重量
	private Double postage;			//邮费
	private String templateName; 	//所属模板名称
	private Long templateId;
	private Integer productType;   //商品类型
	private String areaIds;
	
	public static TemplateRegionVo getIntance(String strPostage, String templateName){
		TemplateRegionVo postageVo = new TemplateRegionVo ();
		List<String> postageList = StringUtil.strToStrList(strPostage, ",");
		if(postageList != null && postageList.size() > 0){
			List<String> areaCodeList = StringUtil.strToStrList(postageList.get(0), "&");
			if(areaCodeList != null && areaCodeList.size() > 0){
				for(String areaCode : areaCodeList){
					postageVo.getAreaIdList().add(StringUtil.nullToLong(areaCode));
				}
			}
            postageVo.setTemplateName(templateName);
            
            if(postageList != null && postageList.size() >= 6){
            	postageVo.setFirstWeigth(StringUtil.nullToDouble(postageList.get(1)));		//首重
    			postageVo.setFirstPrice(StringUtil.nullToDouble(postageList.get(2)));		//运费
    			postageVo.setAfterWeigth(StringUtil.nullToDouble(postageList.get(3)));		//续重
    			postageVo.setAfterPrice(StringUtil.nullToDouble(postageList.get(4)));		//续费
    			postageVo.setPackageWeigth(StringUtil.nullToDouble(postageList.get(5)));	//包材
            }
		}
		return postageVo;
	}
	
	public List<Long> getAreaIdList() {
		return areaIdList;
	}

	public void setAreaIdList(List<Long> areaIdList) {
		this.areaIdList = areaIdList;
	}
	

	public Double getFirstWeigth() {
		return firstWeigth;
	}

	public void setFirstWeigth(Double firstWeigth) {
		this.firstWeigth = firstWeigth;
	}

	public Double getFirstPrice() {
		return firstPrice;
	}

	public void setFirstPrice(Double firstPrice) {
		this.firstPrice = firstPrice;
	}

	public Double getAfterWeigth() {
		return afterWeigth;
	}

	public void setAfterWeigth(Double afterWeigth) {
		this.afterWeigth = afterWeigth;
	}

	public Double getAfterPrice() {
		return afterPrice;
	}

	public void setAfterPrice(Double afterPrice) {
		this.afterPrice = afterPrice;
	}

	public void setPackageWeigth(Double packageWeigth) {
		this.packageWeigth = packageWeigth;
	}

	public Double getPostage() {
		return postage;
	}

	public void setPostage(Double postage) {
		this.postage = postage;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getAreaIds() {
		return areaIds;
	}

	public void setAreaIds(String areaIds) {
		this.areaIds = areaIds;
	}


	public Double getPackageWeigth() {
		return packageWeigth;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}
}
