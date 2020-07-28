package com.chunruo.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 用户表
 * @author chunruo
 */
@Entity
@Table(name="jkd_user_info",uniqueConstraints = {
	@UniqueConstraint(columnNames = {"mobile", "country_code"})
})
public class UserInfo implements Cloneable, Serializable{
	private static final long serialVersionUID = -2259459843973883469L;
	public static final String DEFUALT_COUNTRY_CODE = "86"; //默认中国区号
	
	// 登录类			
	public static final int LOGIN_TYPE_JKD_PROGRAM = 5;		//纯若小程序登录
	
	private Long userId;						//用户ID
	private String unionId;					    //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
	private String oldUnionId;					//旧的unionId必须保留兼容之前用户正常登陆
    private String openId;					    //授权openId(纯若)
    private String nickname;					//昵称
    private String password;					//密码
    private String countryCode;				    //国家区号(默认+86)
    private String mobile;					    //手机号码
    private Boolean isAgent;					//是否代理商(卖家)
    private String registerIp;				    //注册IP地址
    private String lastIp;					    //登陆最后IP地址
    private Integer loginCount;				    //登陆次数
    private Boolean status;					    //账号是否启用
    private String introduce;				    //个人签名
    private String headerImage;				    //头像地址
    private Integer sex;						//性别
    private Long provinceId;					//省份
    private Long cityId;						//城市
    private Long areaId;						//区域
    private String realName;					//真实姓名
    private String identityNo;				    //身份证号码
    private String expireEndDate;   			//到期结束日期
	private Integer level;					    //级别(1:店长;2:经销商;3:总代)
    private Date lastLoginTime;				    //最后登录时间
    private String weixinCardImage;			    //用户微信名片
    private String payPassword;                 //支付密码
    private Integer chanceCount;                //剩余输入密码机会次数
    private Date inputTime;                     //安全密码输入时间
    private Integer hintStatus;            	    //申请状态
    private Boolean isBindWechat;				//是否绑定APP微信
    private String wechatNick;					//绑定微信昵称
    private Long userManagerId;                 //客户经理id
    private Date registerTime;                  //注册时间
    private Date upgradeTime;                   //升级时间
    private Long topUserId;					    //上级ID
    private Long shareUserId;                   //分享人ID
    private Date createTime;				    //创建时间
    private Date updateTime;				    //更新时间
    
    // 用户店铺信息
    private String storeName;				//店铺名称
    private String storeMobile;             //店铺电话
    private String linkman;                 //联系人
    private Double balance;					//可提现金额（集币）
    private Double sales;					//销售金额
    private Double income;					//累计销售金额
    private String inviterCode;				//邀请码
    private Double withdrawalAmount; 		//已提现金额
	private Long bankId; 					//开户银行
	private String bankCard; 				//银行卡号
	private String bankCardUser; 			//开卡人姓名
	private String openingBank; 			//开户行
	private Double accountAmount;           //账户数额（充值）
	private Date inviteCodeEndTime;         //邀请码到期时间
	
	private Boolean isAuthSucc = false;     //是否认证成功
	private String idCardName;              //身份证姓名
	private String idCardNo;                //身份证号码
	private Date authTime;                  //认证时间
	//Transient
	private String provinceName;			//省份名称
	private String cityName;				//城市名称
	private String areaName;				//区域名称
	private String bankName;				//银行名称
	private String topStoreName; 			//上级店铺名称
	private String topMobile;               //上级账号
	private Integer isHavePassword;     	//是否绑定密码
	private Boolean isCommonUserLevel;		//是否H5普通用户
	private String vipCustomerId;			//VIP用户客服ID
	private String topUserInviteCode;  		//上级店铺邀请码
	private Boolean isNeedSendMsg;      	//是否需要发送短信
	private Integer inviteV1Number;         //邀请的v1人数 
	private ProductShareRecord productShareRecord; //分享信息
	private Boolean isShareUser;            //是否分享用户
	private Double curDaySaleAmount;        //今日销售额
	private Double curMonthSaleAmount;      //本月销售额
	private Double lastMonthSaleAmount;     //上月销售额
	private Double lastMonthRefundAmount;   //上月退款额
	private Boolean isTemporaryMember;      //是否临时会员
	private Long memberEndTime;             //临时会员有效期
	private Integer paymentUserLevel;       //用户实时等级
	private Integer loginType;				//用户登录类型
	private String loginPcIp;				//PC登陆IP地址
	private String logo;                    
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Column(name="union_id")
	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
	@Column(name = "old_union_id", nullable=false)
	public String getOldUnionId() {
		return oldUnionId;
	}

	public void setOldUnionId(String oldUnionId) {
		this.oldUnionId = oldUnionId;
	}

	@Column(name="open_id")
    public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	@Column(name="nick_name")
    public String getNickname() {
        return nickname;
    }

	public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column(name="pass_word")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="country_code", length=5)
    public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name="mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    @Column(name="is_agent")
    public Boolean getIsAgent() {
		return isAgent;
	}

	public void setIsAgent(Boolean isAgent) {
		this.isAgent = isAgent;
	}
	
	@Column(name="store_name")
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@Column(name="balance")
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Column(name="sales")
	public Double getSales() {
		return sales;
	}

	public void setSales(Double sales) {
		this.sales = sales;
	}
	
	@Column(name="income")
	public Double getIncome() {
		return income;
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	@Column(name="account_amount")
	public Double getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(Double accountAmount) {
		this.accountAmount = accountAmount;
	}

	@Column(name="register_ip")
	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	@Column(name="last_ip")
	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	@Column(name="login_count")
	public Integer getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}
	
	@Column(name = "last_login_time")
    public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Column(name="status")
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	

	@Column(name="user_manager_id")
	public Long getUserManagerId() {
		return userManagerId;
	}

	public void setUserManagerId(Long userManagerId) {
		this.userManagerId = userManagerId;
	}

	@Column(name="introduce")
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	@Column(name="header_image")
	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	@Column(name="sex")
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name="province_id")
	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	@Column(name="city_id")
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	@Column(name="area_id")
	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	
	@Column(name="real_name")
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	@Column(name="identity_no")
	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	@Column(name="top_user_id")
	public Long getTopUserId() {
		return topUserId;
	}

	public void setTopUserId(Long topUserId) {
		this.topUserId = topUserId;
	}
	
	@Column(name="share_user_id")
	public Long getShareUserId() {
		return shareUserId;
	}

	public void setShareUserId(Long shareUserId) {
		this.shareUserId = shareUserId;
	}

	@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="update_time")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Column(name="register_time")
	public Date getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}

	@Column(name="upgrade_time")
	public Date getUpgradeTime() {
		return upgradeTime;
	}

	public void setUpgradeTime(Date upgradeTime) {
		this.upgradeTime = upgradeTime;
	}

	@Column(name = "expire_end_date")
	public String getExpireEndDate() {
		return expireEndDate;
	}

	public void setExpireEndDate(String expireEndDate) {
		this.expireEndDate = expireEndDate;
	}

	@Column(name = "is_auth_succ")
	public Boolean getIsAuthSucc() {
		return isAuthSucc;
	}

	public void setIsAuthSucc(Boolean isAuthSucc) {
		this.isAuthSucc = isAuthSucc;
	}

	@Column(name = "id_card_name")
	public String getIdCardName() {
		return idCardName;
	}

	public void setIdCardName(String idCardName) {
		this.idCardName = idCardName;
	}

	@Column(name = "id_card_no")
	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	@Column(name = "invite_code_end_time")
	public Date getInviteCodeEndTime() {
		return inviteCodeEndTime;
	}

	public void setInviteCodeEndTime(Date inviteCodeEndTime) {
		this.inviteCodeEndTime = inviteCodeEndTime;
	}

	@Column(name = "auth_time")
	public Date getAuthTime() {
		return authTime;
	}

	public void setAuthTime(Date authTime) {
		this.authTime = authTime;
	}

	@Column(name = "level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Column(name = "weixin_card_image")
    public String getWeixinCardImage() {
		return weixinCardImage;
	}

	public void setWeixinCardImage(String weixinCardImage) {
		this.weixinCardImage = weixinCardImage;
	}
	
	
	@Column(name = "pay_password")
	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	@Column(name = "chance_count")
	public Integer getChanceCount() {
		return chanceCount;
	}

	public void setChanceCount(Integer chanceCount) {
		this.chanceCount = chanceCount;
	}
	
	@Column(name = "is_bind_wechat")
	public Boolean getIsBindWechat() {
		return isBindWechat;
	}

	public void setIsBindWechat(Boolean isBindWechat) {
		this.isBindWechat = isBindWechat;
	}
	
	@Column(name = "wechat_nick", length=100)
	public String getWechatNick() {
		return wechatNick;
	}

	public void setWechatNick(String wechatNick) {
		this.wechatNick = wechatNick;
	}

	@Column(name = "input_time")
	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	@Column(name = "hint_status")
	public Integer getHintStatus() {
		return hintStatus;
	}

	public void setHintStatus(Integer hintStatus) {
		this.hintStatus = hintStatus;
	}
	
	@Column(name = "link_man")
	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	@Column(name = "inviter_code")
	public String getInviterCode() {
		return inviterCode;
	}

	public void setInviterCode(String inviterCode) {
		this.inviterCode = inviterCode;
	}	
	
	@Column(name = "store_mobile")
	public String getStoreMobile() {
		return storeMobile;
	}

	public void setStoreMobile(String storeMobile) {
		this.storeMobile = storeMobile;
	}

	@Column(name = "withdrawal_amount")
	public Double getWithdrawalAmount() {
		return withdrawalAmount;
	}

	public void setWithdrawalAmount(Double withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}

	@Column(name = "bank_id")
	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	@Column(name = "bank_card")
	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard == null ? null : bankCard.trim();
	}

	@Column(name = "bank_card_user")
	public String getBankCardUser() {
		return bankCardUser;
	}

	public void setBankCardUser(String bankCardUser) {
		this.bankCardUser = bankCardUser == null ? null : bankCardUser.trim();
	}

	@Column(name = "opening_bank")
	public String getOpeningBank() {
		return openingBank;
	}

	public void setOpeningBank(String openingBank) {
		this.openingBank = openingBank == null ? null : openingBank.trim();
	}

	@Transient
	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	@Transient
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Transient
	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	@Transient
	public String getTopStoreName() {
		return topStoreName;
	}

	public void setTopStoreName(String topStoreName) {
		this.topStoreName = topStoreName;
	}

	@Transient
	public String getTopMobile() {
		return topMobile;
	}

	public void setTopMobile(String topMobile) {
		this.topMobile = topMobile;
	}

	@Transient
	public Integer getIsHavePassword() {
		return isHavePassword;
	}

	public void setIsHavePassword(Integer isHavePassword) {
		this.isHavePassword = isHavePassword;
	}

	@Transient
	public Boolean getIsCommonUserLevel() {
		return isCommonUserLevel;
	}

	public void setIsCommonUserLevel(Boolean isCommonUserLevel) {
		this.isCommonUserLevel = isCommonUserLevel;
	}

	@Transient
	public String getVipCustomerId() {
		return vipCustomerId;
	}

	public void setVipCustomerId(String vipCustomerId) {
		this.vipCustomerId = vipCustomerId;
	}

	@Transient
	public String getTopUserInviteCode() {
		return topUserInviteCode;
	}

	public void setTopUserInviteCode(String topUserInviteCode) {
		this.topUserInviteCode = topUserInviteCode;
	}

	@Transient
	public Boolean getIsNeedSendMsg() {
		return isNeedSendMsg;
	}

	public void setIsNeedSendMsg(Boolean isNeedSendMsg) {
		this.isNeedSendMsg = isNeedSendMsg;
	}

	@Transient
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Transient
	public Integer getInviteV1Number() {
		return inviteV1Number;
	}

	public void setInviteV1Number(Integer inviteV1Number) {
		this.inviteV1Number = inviteV1Number;
	}

	@Transient
	public ProductShareRecord getProductShareRecord() {
		return productShareRecord;
	}

	public void setProductShareRecord(ProductShareRecord productShareRecord) {
		this.productShareRecord = productShareRecord;
	}
	
	@Transient
	public Boolean getIsShareUser() {
		return isShareUser;
	}

	public void setIsShareUser(Boolean isShareUser) {
		this.isShareUser = isShareUser;
	}

	@Transient
	public Double getCurDaySaleAmount() {
		return curDaySaleAmount;
	}

	public void setCurDaySaleAmount(Double curDaySaleAmount) {
		this.curDaySaleAmount = curDaySaleAmount;
	}

	@Transient
	public Double getCurMonthSaleAmount() {
		return curMonthSaleAmount;
	}

	public void setCurMonthSaleAmount(Double curMonthSaleAmount) {
		this.curMonthSaleAmount = curMonthSaleAmount;
	}

	@Transient
	public Double getLastMonthSaleAmount() {
		return lastMonthSaleAmount;
	}

	public void setLastMonthSaleAmount(Double lastMonthSaleAmount) {
		this.lastMonthSaleAmount = lastMonthSaleAmount;
	}

	@Transient
	public Double getLastMonthRefundAmount() {
		return lastMonthRefundAmount;
	}

	public void setLastMonthRefundAmount(Double lastMonthRefundAmount) {
		this.lastMonthRefundAmount = lastMonthRefundAmount;
	}

	@Transient
	public Boolean getIsTemporaryMember() {
		return isTemporaryMember;
	}

	public void setIsTemporaryMember(Boolean isTemporaryMember) {
		this.isTemporaryMember = isTemporaryMember;
	}

	@Transient
	public Long getMemberEndTime() {
		return memberEndTime;
	}

	public void setMemberEndTime(Long memberEndTime) {
		this.memberEndTime = memberEndTime;
	}

	@Transient
	public Integer getPaymentUserLevel() {
		return paymentUserLevel;
	}

	public void setPaymentUserLevel(Integer paymentUserLevel) {
		this.paymentUserLevel = paymentUserLevel;
	}

	@Transient
	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	@Transient
	public String getLoginPcIp() {
		return loginPcIp;
	}

	public void setLoginPcIp(String loginPcIp) {
		this.loginPcIp = loginPcIp;
	}

	@Transient
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public UserInfo clone(){
		//浅拷贝
		try {
			// 直接调用父类的clone()方法
			return (UserInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}