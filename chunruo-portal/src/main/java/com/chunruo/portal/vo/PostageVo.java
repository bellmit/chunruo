package com.chunruo.portal.vo;

import java.util.ArrayList;
import java.util.List;
import com.chunruo.core.util.StringUtil;

public class PostageVo {
	private List<Long> areaIdList = new ArrayList<Long> ();	// 地区列表
	private Double firstWeigth;	//首重
	private Double firstPrice;	//运费
	private Double afterWeigth;	//续重
	private Double afterPrice;	//续费
	private Double postage;		//邮费
	private Double packageWeigth;  //包材重量
	private String areaNames;   //地区名称
	private String priceNotice; //价格说明
	public static PostageVo getIntance(String strPostage){
		PostageVo postageVo = new PostageVo ();
		List<String> postageList = StringUtil.strToStrList(strPostage, ",");
		if(postageList != null && postageList.size() > 0){
			List<String> areaCodeList = StringUtil.strToStrList(postageList.get(0), "&");
			if(areaCodeList != null && areaCodeList.size() > 0){
				for(String areaCode : areaCodeList){
					postageVo.getAreaIdList().add(StringUtil.nullToLong(areaCode));
				}
			}
			
			postageVo.setFirstWeigth(StringUtil.nullToDouble(postageList.get(1)));	//首重
			postageVo.setFirstPrice(StringUtil.nullToDouble(postageList.get(2)));	//运费
			postageVo.setAfterWeigth(StringUtil.nullToDouble(postageList.get(3)));	//续重
			postageVo.setAfterPrice(StringUtil.nullToDouble(postageList.get(4)));	//续费
			postageVo.setPackageWeigth(StringUtil.nullToDouble(postageList.get(5)));//包材
			
		}
		return postageVo;
	}
	
	public List<Long> getAreaIdList() {
		return areaIdList;
	}

	public void setAreaIdList(List<Long> areaIdList) {
		this.areaIdList = areaIdList;
	}
	
	public Double getFirstPrice() {
		return firstPrice;
	}
	
	public void setFirstPrice(Double firstPrice) {
		this.firstPrice = firstPrice;
	}
	
	public Double getAfterPrice() {
		return afterPrice;
	}
	
	public void setAfterPrice(Double afterPrice) {
		this.afterPrice = afterPrice;
	}

	public Double getPostage() {
		return postage;
	}

	public void setPostage(Double postage) {
		this.postage = postage;
	}

	public String getAreaNames() {
		return areaNames;
	}

	public void setAreaNames(String areaNames) {
		this.areaNames = areaNames;
	}

	public String getPriceNotice() {
		return priceNotice;
	}

	public void setPriceNotice(String priceNotice) {
		this.priceNotice = priceNotice;
	}

	public Double getAfterWeigth() {
		return afterWeigth;
	}

	public void setAfterWeigth(Double afterWeigth) {
		this.afterWeigth = afterWeigth;
	}

	public Double getPackageWeigth() {
		return packageWeigth;
	}

	public void setPackageWeigth(Double packageWeigth) {
		this.packageWeigth = packageWeigth;
	}

	public void setFirstWeigth(Double firstWeigth) {
		this.firstWeigth = firstWeigth;
	}

	public Double getFirstWeigth() {
		return firstWeigth;
	}

}
