package com.chunruo.portal.vo;

import java.io.Serializable;
import java.util.Map;

import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XmlParseUtil;

/**
 * 宏巍ERP接口交互基础工具类
 * @author chunruo
 *
 */
public class HBaseObject implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Boolean isSuccess;
	private Integer errorCode;
	private String errorMsg;
	private String errorNumber;
	private Object object;
	private Integer totalCount;
	
	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Integer getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getErrorNumber() {
		return errorNumber;
	}
	
	public void setErrorNumber(String errorNumber) {
		this.errorNumber = errorNumber;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	public static HBaseObject getIntance(String strXML){
		HBaseObject hwBaseObject = new HBaseObject ();
		return hwBaseObject.getXMLToObject(strXML, null);
	}

	public static HBaseObject getIntance(String strXML, String keyword){
		HBaseObject hwBaseObject = new HBaseObject ();
		return hwBaseObject.getXMLToObject(strXML, keyword);
	}
	
	@SuppressWarnings("unchecked")
	public HBaseObject getXMLToObject(String strXML, String keyword) {
		HBaseObject resultObject = new HBaseObject ();
		resultObject.setIsSuccess(false);
		try{
			Map<String, Object> resultToMap = XmlParseUtil.xmlCont2Map(strXML);
			if(resultToMap != null && resultToMap.containsKey("isSuccess")){
				resultObject.setIsSuccess(StringUtil.nullToBoolean(resultToMap.get("isSuccess")));
				if(!StringUtil.nullToBoolean(resultObject.getIsSuccess())){
					if(resultToMap.containsKey("errorCode")){resultObject.setErrorCode(StringUtil.nullToInteger(resultToMap.get("errorCode")));}
					if(resultToMap.containsKey("errorMsg")){resultObject.setErrorMsg(StringUtil.null2Str(resultToMap.get("errorMsg")));}
					if(resultToMap.containsKey("errorNumber")){resultObject.setErrorNumber(StringUtil.null2Str(resultToMap.get("errorNumber")));}
					return resultObject;
				}else if(!StringUtil.isNull(keyword) && resultToMap.containsKey(keyword)){
					if(resultToMap.containsKey("totalCount")){
						// 订单列表总数量
						resultObject.setTotalCount(StringUtil.nullToInteger(resultToMap.get("totalCount")));
					}
					resultObject.setObject(resultToMap.get(keyword));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			resultObject.setErrorMsg(e.getMessage());
			resultObject.setIsSuccess(false);
		}
		return resultObject;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(" [");
		sb.append("isSuccess").append("='").append(getIsSuccess()).append("', ");
		sb.append("errorCode").append("='").append(getErrorCode()).append("', ");
		sb.append("errorMsg").append("='").append(getErrorMsg()).append("', ");
		sb.append("errorNumber").append("='").append(getErrorNumber()).append("'");
		sb.append("]");
		return sb.toString();
	}
}
