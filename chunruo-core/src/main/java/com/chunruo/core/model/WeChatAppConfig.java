package com.chunruo.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 微信应用列表
 * @author chunruo
 *
 */
@Entity
@Table(name="jkd_wechat_app_config",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"app_id"})
})
public class WeChatAppConfig {
	private Long configId;			//序号
	private String appName;			//应用名称
	private String typeCode;			//应用类型编码
	private String appId;			//公众账号ID
	private String appSecret;		//公众账号秘钥
	private String mchId;			//商户号
	private String secretKey;		//秘钥 默认为MD5，支持HMAC-SHA256和MD5。
	private String tradeType;		//交易类型取值如下：JSAPI，NATIVE，APP等，说明详见参数规定
	private String acceptPayName;	//微信付款账号
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getConfigId() {
		return configId;
	}
	
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	
	@Column(name = "app_name", nullable=false)
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	@Column(name = "type_code", nullable=false)
	public String getTypeCode() {
		return typeCode;
	}
	
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	
	@Column(name = "app_id", nullable=false)
	public String getAppId() {
		return appId;
	}
	
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	@Column(name = "app_secret", nullable=false)
	public String getAppSecret() {
		return appSecret;
	}
	
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	@Column(name = "mch_id", nullable=false)
	public String getMchId() {
		return mchId;
	}
	
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	
	@Column(name = "secret_key", nullable=false)
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	@Column(name = "trade_type", nullable=false)
	public String getTradeType() {
		return tradeType;
	}
	
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	@Column(name = "accept_pay_Name", nullable=false)
	public String getAcceptPayName() {
		return acceptPayName;
	}

	public void setAcceptPayName(String acceptPayName) {
		this.acceptPayName = acceptPayName;
	}
}
