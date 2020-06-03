package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.Constants.WechatOautType;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Bank;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.model.UserTeam;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.repository.UserInfoRepository;
import com.chunruo.core.service.BankManager;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.vo.TeamDataVo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userInfoManager")
public class UserInfoManagerImpl extends GenericManagerImpl<UserInfo, Long> implements UserInfoManager{
	private UserInfoRepository userInfoRepository;
	private Lock lock = new ReentrantLock();
	@Autowired
	private UserSocietyManager userSocietyManager;
	@Autowired
	private BankManager bankManager;
	
	@Autowired
	public UserInfoManagerImpl(UserInfoRepository userInfoRepository) {
		super(userInfoRepository);
		this.userInfoRepository = userInfoRepository;
	}

	@Override
	public UserInfo getUserInfoByMobile(String mobile, String countryCode) {
		return this.userInfoRepository.getUserInfoByMobile(mobile, countryCode);
	}
	
	@Override
	public List<UserInfo> getUserInfoListByUpdateTime(Date updateTime) {
		return this.userInfoRepository.getUserInfoListByUpdateTime(updateTime);
	}
	
	@Override
	public void updateUserHeaderImage(String headerImage, Long userId) {
		this.userInfoRepository.updateUserHeaderImage(headerImage, userId);
	}

	@Override
	public UserInfo getUserInfoByOpenId(String openId) {
		List<UserInfo> userInfoList = this.userInfoRepository.getUserInfoByOpenId(openId);
		return CollectionUtils.isEmpty(userInfoList) ? null : userInfoList.get(0);
	}

	@Override
	public UserInfo getUserInfoByUnionId(String unionId) {
		List<UserInfo> userInfoList = this.userInfoRepository.getUserInfoByUnionId(unionId);
		return CollectionUtils.isEmpty(userInfoList) ? null : userInfoList.get(0);
	}
	
	@Override
	public UserInfo getUserInfoByOldUnionId(String oldUnionId) {
		List<UserInfo> userInfoList = this.userInfoRepository.getUserInfoByOldUnionId(oldUnionId);
		return CollectionUtils.isEmpty(userInfoList) ? null : userInfoList.get(0);
	}

	@Override
	public UserInfo getUserInfoByInviterCode(String inviterCode) {
		List<UserInfo> list = this.userInfoRepository.getUserInfoListByInviterCode(inviterCode);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public boolean isExitsByMobile(String mobile, String countryCode) {
		UserInfo userInfo = this.getUserInfoByMobile(mobile, countryCode);
		if(userInfo != null && userInfo.getUserId() != null){
			return true;
		}
		return false;
	}
	
	@Override
	public List<UserInfo> getUserInfoListByTopUserId(Long topUserId) {
		return this.userInfoRepository.getUserInfoListByTopUserId(topUserId);
	}

	@Override
	public UserInfo saveAgent(int oauthType, Long topUserId, String mobile, String countryCode, UserSociety userSociety) {
		UserInfo userInfo = null;
		if(StringUtil.compareObject(oauthType, WechatOautType.WECHAT_OAUTH_TYPE_NEW)){
			String openId = StringUtil.null2Str(userSociety.getOpenId());
			UserSociety userSocietyBak = this.userSocietyManager.getUserSocietyByOpenIdAndConfigId(Constants.APP_CLIENT_WECHAT_CONFIG_ID, openId);
			//微信信息存在，用户信息不存在
			if(userSocietyBak == null || userSocietyBak.getUserSocietyId() == null) {
				// 全新用户注册为代理
				userSociety.setCreateTime(DateUtil.getCurrentDate());		// 创建时间
				userSociety.setUpdateTime(userSociety.getCreateTime());		// 更新时间
				userSociety = this.userSocietyManager.save(userSociety);
			}
			
			userInfo = new UserInfo ();
			userInfo.setHeaderImage(userSociety.getHeadImgUrl());		                //用户头像
			userInfo.setNickname(userSociety.getNickname()); 			                //用户昵称
			userInfo.setMobile(mobile);									                //手机号码
			userInfo.setCountryCode(countryCode);						                //国家区域编码
			userInfo.setIsAgent(true);									                //是否为代销
			userInfo.setTopUserId(topUserId);							                //上级用户ID
			userInfo.setUnionId(userSociety.getUnionId());				                //微信联盟ID
			userInfo.setStatus(true);									                //账号是否启用
			userInfo.setLevel(UserLevel.USER_LEVEL_BUYERS);                             //VIP
			userInfo.setOpenId(userSociety.getOpenId());                                //微信openid
			userInfo.setLoginCount(0);													//登陆次数
			userInfo.setSex(StringUtil.nullToInteger(userSociety.getSex()));			//性别
			userInfo.setRegisterIp(StringUtil.null2Str(userSociety.getClientIp()));		//注册IP地址
			userInfo.setLastIp(StringUtil.null2Str(userSociety.getClientIp()));			//登陆最后IP地址
			userInfo.setStoreName(userSociety.getNickname());
			userInfo.setStoreMobile(mobile);
			userInfo.setWithdrawalAmount(0d);
			userInfo.setSales(0d);
			userInfo.setIncome(0d);
			userInfo.setIsBindWechat(true);
			userInfo.setWechatNick(StringUtil.null2Str(userSociety.getNickname()));
			//生成邀请码
			userInfo.setInviterCode(StringUtil.null2Str(this.getInveterCodeList(1).get(0)));
			userInfo.setCreateTime(DateUtil.getCurrentDate());			                //创建时间
			userInfo.setRegisterTime(DateUtil.getCurrentDate());                         //注册时间
			userInfo.setUpdateTime(userInfo.getCreateTime());			                //更新时间
			userInfo = this.save(userInfo);
		}else{
			userInfo = this.get(userSociety.getUserId());
			if(StringUtil.compareObject(oauthType, WechatOautType.WECHAT_OAUTH_TYPE_AGENT)){
				//微信授权普通用户转换成代理商
				userInfo.setMobile(mobile);							//手机号码
				userInfo.setCountryCode(countryCode);				//国家区域编码
				userInfo.setIsAgent(true);							//是否为代销
				userInfo.setStatus(true);
				userInfo.setLevel(UserLevel.USER_LEVEL_BUYERS);     //VIP
				userInfo.setTopUserId(topUserId);
				userInfo.setStoreName(userSociety.getNickname());
				userInfo.setStoreMobile(mobile);
				userInfo.setWithdrawalAmount(0d);
				userInfo.setSales(0d);
				userInfo.setIncome(0d);
				//生成邀请码
				userInfo.setInviterCode(StringUtil.null2Str(this.getInveterCodeList(1).get(0)));  // 邀请码
				userInfo.setRegisterTime(DateUtil.getCurrentDate());                               //注册时间
				userInfo.setIsBindWechat(true);
				userInfo.setWechatNick(StringUtil.null2Str(userSociety.getNickname()));
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				userInfo = this.update(userInfo);
			}else if(StringUtil.compareObject(oauthType, WechatOautType.WECHAT_OAUTH_TYPE_MOBILE)){
				//微信授权代理商部手机号码
				userInfo.setMobile(mobile);
				userInfo.setStoreMobile(mobile);
				userInfo.setCountryCode(countryCode);
				userInfo.setRegisterTime(DateUtil.getCurrentDate());                               //注册时间
				userInfo.setIsBindWechat(true);
				userInfo.setWechatNick(StringUtil.null2Str(userSociety.getNickname()));
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				userInfo = this.update(userInfo);
			}
		}
		return userInfo;
	}
	
	@Override
	public void updateUserInfo(String unionId ,Long userId){
		this.userInfoRepository.updateUserInfo(unionId, userId);
	}

	@Override
	public UserInfo updateUserInfo(Long userId, UserSociety userSociety, boolean isDelUpdate) {
		//是否删除绑定再更新绑定关系
		if(StringUtil.nullToBoolean(isDelUpdate)) {
			List<UserSociety> userSocietyList = this.userSocietyManager.getUserSocietyByUnionId(userSociety.getUnionId());
			if(userSocietyList != null && userSocietyList.size() > 0) {
				// 原有用户和授权关系清除,保留用户信息
				UserInfo userInfo = this.get(userSocietyList.get(0).getUserId());
				if(userInfo != null && userInfo.getUserId() != null) {
					userInfo.setUnionId("");
					userInfo.setIsBindWechat(false);
					userInfo.setUpdateTime(DateUtil.getCurrentDate());
					this.save(userInfo);
				}
				this.userSocietyManager.deleteUserSocietyByUnionId(userSociety.getUnionId());
			}
		}
		
		//微信授权信息保存
		String unionId = userSociety.getUnionId();
		userSociety.setCreateTime(DateUtil.getCurrentDate());		// 创建时间
		userSociety.setUpdateTime(userSociety.getCreateTime());		// 更新时间
		userSociety = this.userSocietyManager.save(userSociety);
		
		UserInfo userInfo = this.get(userId);
		if(userInfo != null && userInfo.getUserId() != null){
			// 删除之前已绑定账号unionId授权记录
			this.userSocietyManager.deleteUserSocietyByUnionId(userInfo.getUnionId());
			
			//用户绑定微信
			userInfo.setIsBindWechat(true);
			userInfo.setWechatNick(StringUtil.null2Str(userSociety.getNickname()));
			userInfo.setUnionId("");
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			this.save(userInfo);
		}
		userInfo.setUnionId(unionId);
		return userInfo;
	}

	@Override
	public List<UserInfo> addAddress(List<UserInfo> userList) {
		if (null != Constants.AREA_MAP && Constants.AREA_MAP.size() > 0) {
			for (UserInfo u : userList) {
				u.setProvinceName(u.getProvinceId() == null ? null : Constants.AREA_MAP.get(u.getProvinceId()).getAreaName());
				u.setCityName(u.getCityId() == null ? null : Constants.AREA_MAP.get(u.getCityId()).getAreaName());
				u.setAreaName(u.getAreaId() == null ? null : Constants.AREA_MAP.get(u.getAreaId()).getAreaName());
			}
		}
		return userList;
	}

	@Override
	public List<UserInfo> getAllAgent() {
		return this.userInfoRepository.getAllAgent();
	}
	
	@Override
	public List<UserInfo> getAgentUserByLevel(Integer level) {
		return this.userInfoRepository.getAgentUserByLevel(level);
	}

	@Override
	public Map<String, Integer> getDownLineCountByMobile(String mobile) {
		Map<String,Integer> countMap = new HashMap<String,Integer>();
		// 一级下线
		StringBuffer firstHql = new StringBuffer();
		firstHql.append("SELECT COUNT(*) FROM jkd_user_info jui WHERE jui.top_user_id IN(");
		firstHql.append(" SELECT ju.user_id FROM jkd_user_info ju WHERE ju.mobile=%s");
		firstHql.append(" ) AND jui.is_agent=1 AND jui.level in(%s)");
		
		//二级下线
		StringBuffer secondHql = new StringBuffer();
		secondHql.append("SELECT COUNT(*) FROM jkd_user_info jkui WHERE jkui.`top_user_id` IN(");
		secondHql.append(" SELECT jui.user_id FROM jkd_user_info jui WHERE jui.top_user_id IN(");
		secondHql.append(" SELECT ju.user_id FROM jkd_user_info ju WHERE ju.mobile=%s");
		secondHql.append(" ) AND jui.is_agent=1 ) AND jkui.is_agent=1 AND jkui.`level` in(%s)");
		long firstDeclareCount = this.countSql(String.format(firstHql.toString(),mobile,"2,3,4,5"));
		long firstAgentCount = this.countSql(String.format(firstHql.toString(),mobile,"3"));
		long secondDeclareCount = this.countSql(String.format(secondHql.toString(),mobile,"2,3,4,5"));
		long secondAgentCount = this.countSql(String.format(secondHql.toString(),mobile,"3"));
	
		countMap.put("firstDeclareCount", StringUtil.nullToInteger(firstDeclareCount));
		countMap.put("firstAgentCount", StringUtil.nullToInteger(firstAgentCount));
		countMap.put("secondDeclareCount", StringUtil.nullToInteger(secondDeclareCount));
		countMap.put("secondAgentCount", StringUtil.nullToInteger(secondAgentCount));
		return countMap;
	}

	@Override
	public void editUserMobile(String oldMobile, String newMobile) {
		this.userInfoRepository.editUserMobile(oldMobile,newMobile);
	}

	@Override
	public List<UserInfo> getSystemUserInfo() {
		return null;
	}

	@Override
	public void setSystemUserInfo(List<Long> userIdList) {
	}

	@Override
	public Integer getDownListCountByUserId(Long userId) {
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT count(*) FROM jkd_user_info jui WHERE jui.top_user_id = %s and jui.is_agent=1 and jui.level=2");
		return StringUtil.nullToInteger(this.countSql(String.format(sqlBuf.toString(),userId)));
	}
	
	@Override
	public TeamDataVo getTeamDataInfo(Long userId) {
		TeamDataVo teamDataVo = new TeamDataVo();
		StringBuffer dirAgentSqlBuffer = new StringBuffer();
		dirAgentSqlBuffer.append("SELECT COUNT(*) FROM jkd_user_info jui WHERE jui.top_user_id IN ");
		dirAgentSqlBuffer.append(" (%s) AND jui.is_agent=TRUE AND jui.level=%s ");
		
		//查询直属经销商数量
		Integer directDeclare = StringUtil.nullToInteger(this.countSql(String.format(dirAgentSqlBuffer.toString(),userId,2)));
		//查询直属总代数量
		Integer directAgent = StringUtil.nullToInteger(this.countSql(String.format(dirAgentSqlBuffer.toString(),userId,3)));
		
		String dateFormat = "%Y-%m";
		//查询直属团队当月新增经销商数量
		StringBuffer dirMonthSqlBuffer = new StringBuffer();
		dirMonthSqlBuffer.append("SELECT COUNT(*) FROM jkd_user_info jui WHERE jui.level=2 ");
		dirMonthSqlBuffer.append("AND jui.`is_agent`=1 AND jui.`user_id` IN( ");
		dirMonthSqlBuffer.append("SELECT juir.user_id FROM jkd_user_invite_record juir ");
		dirMonthSqlBuffer.append("WHERE  DATE_FORMAT( juir.create_time, '%s' ) = DATE_FORMAT( CURDATE( ) , '%s' ) ");
		dirMonthSqlBuffer.append("AND juir.`is_payment_succ`=1 AND juir.`top_user_id` IN(%s)  ");
		dirMonthSqlBuffer.append("AND juir.`record_type`=2 AND juir.invite_type=1) ");
		
		//当月
		Integer directMonth = StringUtil.nullToInteger(this.countSql(String.format(dirMonthSqlBuffer.toString(),dateFormat,dateFormat, userId)));
		
		StringBuffer secMonthSqlBuf = new StringBuffer(); 
		secMonthSqlBuf.append("SELECT jaaa.user_id FROM jkd_user_info jaaa ");
		secMonthSqlBuf.append("WHERE jaaa.`is_agent` = 1 AND jaaa.`top_user_id` = %s ");
		
		//当月新增次级经销商数量
		Integer subMonth = StringUtil.nullToInteger(this.countSql(String.format(dirMonthSqlBuffer.toString(),dateFormat,dateFormat, String.format(secMonthSqlBuf.toString(), userId))));
		//查询次级经销商数量
	    Integer subDeclare = StringUtil.nullToInteger(this.countSql(String.format(dirAgentSqlBuffer.toString(),String.format(secMonthSqlBuf.toString(), userId),2)));
	    //查询次级总代数量
	    Integer subAgent = StringUtil.nullToInteger(this.countSql(String.format(dirAgentSqlBuffer.toString(),String.format(secMonthSqlBuf.toString(), userId),3)));
		
		
		teamDataVo.setDirectAgent(directAgent);
		teamDataVo.setDirectMonth(directMonth);
		teamDataVo.setDirectDeclare(directDeclare);
		teamDataVo.setSubAgent(subAgent);
		teamDataVo.setSubDeclare(subDeclare);
		teamDataVo.setSubMonth(subMonth);
		return teamDataVo;
	}

	@Override
	public List<UserInfo> getCustomerManagerUserInfo() {
		return null;
	}
	
	@Override
	public MsgModel<UserInfo> getTopUserInfoByTopUserId(Long topUserId){
		MsgModel<UserInfo> msgModel = new MsgModel<UserInfo> ();
		try{
			// 支持上级返利等级用户
			UserInfo userInfo = this.get(topUserId);
			if(userInfo != null 
					&& userInfo.getUserId() != null
					&& StringUtil.nullToBoolean(userInfo.getIsAgent())){
				// 支持上下级利润分成用户等级
				List<Integer> agentLevelList = new ArrayList<Integer> ();
				agentLevelList.add(UserLevel.USER_LEVEL_DEALER); //经销商
				agentLevelList.add(UserLevel.USER_LEVEL_AGENT);	 //总代
				agentLevelList.add(UserLevel.USER_LEVEL_V2);	 //V2
				agentLevelList.add(UserLevel.USER_LEVEL_V3);	 //V3
				
				// 检查是否经销商
				if(agentLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
					msgModel.setIsDistributor(true);
				}
				
				msgModel.setIsSucc(true);
				msgModel.setData(userInfo);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}

	@Override
	public List<UserInfo> getUserInfoByTopUserIdList(List<Long> topUserIdList) {
		return this.userInfoRepository.getUserInfoByTopUserIdList(topUserIdList);
	}

	@Override
	public List<UserInfo> addTopStoreInfo(List<UserInfo> userList) {
		try {
			
			if(userList != null && userList.size() > 0) {
				Map<Long, UserInfo> userMap = new HashMap<Long,UserInfo>();
				List<Long> topUserIdList = new ArrayList<Long>();
				Map<Long,Bank> bankMap = new HashMap<Long,Bank>();
				List<Bank> bankList = this.bankManager.getAll();
				if(bankList != null && bankList.size() > 0) {
					for(Bank bank : bankList) {
						bankMap.put(StringUtil.nullToLong(bank.getBankId()), bank);
					}
				}
				for (UserInfo userInfo : userList) {
					Bank bank = bankMap.get(StringUtil.nullToLong(userInfo.getBankId()));
					if(bank != null && bank.getBankId() != null) {
						userInfo.setBankName(StringUtil.null2Str(bank.getName()));
					}
					if(userInfo.getTopUserId() != null && !topUserIdList.contains(userInfo.getTopUserId())) {
						topUserIdList.add(userInfo.getTopUserId());
					}
				}
				
				List<UserInfo> topUserInfoList = this.getByIdList(topUserIdList);
				if(topUserInfoList != null && topUserInfoList.size() > 0) {
					for (UserInfo topUserInfo : topUserInfoList) {
						userMap.put(topUserInfo.getUserId(), topUserInfo);
					}
				}
				
				if(userMap != null && userMap.size() >0 ) {
					for (UserInfo userInfo : userList) {
						UserInfo topUserInfo = userMap.get(userInfo.getTopUserId());
						if(topUserInfo != null) {
							String topStoreName = StringUtil.null2Str(topUserInfo.getStoreName());
							userInfo.setTopStoreName(topStoreName);
							userInfo.setTopMobile(StringUtil.null2Str(topUserInfo.getMobile()));
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	@Override
	public List<String> getInveterCodeList(int size) {
		// 加锁
		lock.lock();
		try {
			Set<String> inviterCodeSet = new HashSet<String>();
			while ( inviterCodeSet.size() < size ) {
				String randomStr = StringUtil.getStringRandom(6).toUpperCase();
				List<UserInfo> list = this.userInfoRepository.getUserInfoListByInviterCode(randomStr);
				if (list == null || list.size() <= 0) {
					// 保证邀请码不重复
					if (!inviterCodeSet.contains(randomStr)) {
						inviterCodeSet.add(randomStr);
					}
				}
			}
			return StringUtil.strSetToList(inviterCodeSet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放锁
			lock.unlock();
		}
		return null;
	}

	@Override
	public void updateUserInfoLevelByLoadFunction() {
		this.userInfoRepository.executeSqlFunction("{?=call updateUserInfoLevel_Fnc()}");
		log.debug("updateUserInfoLevelByLoadFunction======= ");
	}

	@Override
	public List<UserInfo> getUserInfoListByPushLevelList(List<Integer> pushLevelList) {
		return this.userInfoRepository.getUserInfoListByPushLevelList(pushLevelList);
	}

	@Override
	public Map<String,Long> getDownCountByUserIdList(List<Long> userIdList,String beginTime, String endTime) {
		Map<String,Long> map = new HashMap<String,Long>();
		StringBuilder v0Sql = new StringBuilder();
		//v0下线数量
		v0Sql.append("SELECT COUNT(*) v0 FROM jkd_user_info jui1 WHERE jui1.is_agent = 1 AND jui1.`level` = 1 AND jui1.top_user_id in (%s) ");
		v0Sql.append("AND jui1.register_time BETWEEN '%s' AND '%s'");
		
		StringBuilder v1Sql = new StringBuilder();
		v1Sql.append("SELECT COUNT(*) FROM jkd_user_info jui1 WHERE jui1.`is_agent` = 1 AND jui1.`level` = 2 AND ");
		v1Sql.append("jui1.`user_id` IN(");
		v1Sql.append("SELECT juir1.`user_id` FROM jkd_user_invite_record juir1 WHERE juir1.`is_payment_succ` = 1 AND juir1.top_user_id in(%s)");
		v1Sql.append("AND juir1.create_time BETWEEN '%s' AND '%s' )");
		
		long v0Count = this.countSql(String.format(v0Sql.toString(), StringUtil.longListToStr(userIdList),beginTime,endTime));
		long v1Count = this.countSql(String.format(v1Sql.toString(), StringUtil.longListToStr(userIdList),beginTime,endTime));
        map.put("v0Count", v0Count);
        map.put("v1Count", v1Count);
        return map;
	}

	@Override
	public List<UserInfo> getDecleareUserList() {
		return this.userInfoRepository.getDecleareUserList();
	}
	
	

	
	@Override
	public void batchRegisterUserInfo(List<UserInfo> userInfoList, Long topUserId ,MsgModel<Integer> msgModel) {
	}
	
	/**
	 * 新建用户团队信息
	 * @param userInfo
	 * @return
	 */
	public UserTeam saveUserTeam(UserInfo userInfo) {
		if(userInfo != null && userInfo.getUserId() != null) {
			UserTeam userTeam = new UserTeam();
			userTeam = new UserTeam();
			userTeam.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
			userTeam.setLogo(StringUtil.null2Str(userInfo.getHeaderImage()));
			//新建用户补充信息
			userTeam.setExpireEndDate(userInfo.getExpireEndDate());
			userTeam.setUserCreateTime(userInfo.getRegisterTime());
			userTeam.setTopUserId(StringUtil.nullToLong(userInfo.getTopUserId()));
			userTeam.setStoreName(StringUtil.null2Str(userInfo.getStoreName()));
			userTeam.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));
			userTeam.setUpdateTime(DateUtil.getCurrentDate());
			userTeam.setCreateTime(DateUtil.getCurrentDate());
			return userTeam;
		}
		return null;
	}

	@Override
	public List<UserInfo> getUserInfoListByUserIdList(List<Long> userIdList) {
		return this.userInfoRepository.getUserInfoListByUserIdList(userIdList);
	}

	@Override
	public MsgModel<Void> updateTopUser(Long userId, String newMobile) {
		MsgModel<Void> msgModel = new MsgModel<Void>();
		return msgModel;
	}

	@Override
	public void insertUserV2ExpireEndDateByFunction() {
		this.userInfoRepository.executeSqlFunction("{?=call insertIntoUserV2ExpireEndDate_Fnc(?)}", new Object[] { "insert" });
		log.debug("insertIntoUserV2ExpireEndDate_Fnc=======>>> ");
	}

	@Override
	public void updateUserV2ToV1ByFunction() {
		this.userInfoRepository.executeSqlFunction("{?=call updateUserV2ToV1_Fnc(?)}", new Object[] { "update" });
		log.debug("updateUserV2ToV1_Fnc=======>>> ");
	}

	@Override
	public UserInfo generateInviteCode(UserInfo userInfo) {
		userInfo.setInviterCode(StringUtil.null2Str(this.getInveterCodeList(1).get(0)));
        userInfo.setInviteCodeEndTime(DateUtil.getMonthAfterByDay(DateUtil.getCurrentDate(), 1));
        userInfo.setUpdateTime(DateUtil.getCurrentDate());
        return this.save(userInfo);
	}

	@Override
	public void updateUserPushLevelByFunction() {
		this.userInfoRepository.executeSqlFunction("{?=call updateUserPushLevel_Fnc()}");
		log.debug("updateUserPushLevel_Fnc=======>>> ");
	}

	@Override
	public void cleanBalance() {
		UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
		List<UserInfo> userInfoList = this.userInfoRepository.getUserInfoListByTwoMonthExpire();
		if(userInfoList != null && !userInfoList.isEmpty()) {
			for(UserInfo userInfo : userInfoList) {
				userInfoManager.updateUserInfoBalance(userInfo);
			}
		}
	}
	
	
	@Transactional
	public void updateUserInfoBalance(UserInfo userInfo) {
		UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
		UserWithdrawalManager userWithdrawalManager = Constants.ctx.getBean(UserWithdrawalManager.class);
		UserAmountChangeRecordManager userAmountChangeRecordManager = Constants.ctx.getBean(UserAmountChangeRecordManager.class);

		if(userInfo != null && userInfo.getUserId() != null
				&& StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_BUYERS)
				&& StringUtil.nullToDouble(userInfo.getBalance()).compareTo(0D) > 0) {
			Double amount = StringUtil.nullToDoubleFormat(userInfo.getBalance());
			userInfo.setBalance(0D);
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			userInfoManager.save(userInfo);
			
			//提现记录
			UserWithdrawal userWithdrawal = new UserWithdrawal();
			userWithdrawal.setAmount(amount);
			userWithdrawal.setRemarks("清零");
			userWithdrawal.setStatus(Constants.WithdrawalStatus.SUCCESS_STATUS);
			userWithdrawal.setTradeNo(CoreInitUtil.getRandomNo());
			userWithdrawal.setUserId(userInfo.getUserId());
			userWithdrawal.setCreateTime(DateUtil.getCurrentDate());
			userWithdrawal.setUpdateTime(userWithdrawal.getCreateTime());
			userWithdrawal = userWithdrawalManager.save(userWithdrawal);
			
			//提现记录变更
			// 查询结算记录表是否为空
			List<UserAmountChangeRecord> recordList = userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userWithdrawal.getRecordId(),UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL);
			if (CollectionUtils.isEmpty(recordList)) {
				// 店铺金额变动记录
				UserAmountChangeRecord userAmountChangeRecord = new UserAmountChangeRecord();
				userAmountChangeRecord.setChangeAmount(amount);
				userAmountChangeRecord.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
				userAmountChangeRecord.setBeforeAmount(amount);
				userAmountChangeRecord.setAfterAmount(userInfo.getBalance());
				userAmountChangeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_CLEAN);
				userAmountChangeRecord.setObjectId(StringUtil.nullToLong(userWithdrawal.getRecordId()));
				userAmountChangeRecord.setCreateTime(DateUtil.getCurrentDate());
				userAmountChangeRecord.setUpdateTime(userAmountChangeRecord.getCreateTime());
				userAmountChangeRecordManager.save(userAmountChangeRecord);
			}	
		}
	}

	@Override
	public List<UserInfo> getUserInfoListByIsImportIm(boolean isImportIm) {
		return null;
	}

	@Override
	public void updateUserInfoByIsImportIm(List<Long> userIdList, boolean isImportIm) {
	}

	@Override
	public void updateUserInfoByIsPortraitSet(List<Long> userIdList, boolean isPortraitSet) {
	}

	@Override
	public List<UserInfo> getOperatorUserInfo() {
		return null;
	}
	
	@Override
	public List<UserInfo> getBdUserInfo() {
		return this.userInfoRepository.getBdUserInfo();
	}

	@Override
	public List<UserInfo> getUserInfoListByLevelList(List<Integer> levelList) {
		return this.userInfoRepository.getUserInfoListByLevelList(levelList);
	}

	@Override
	public List<UserInfo> getUserInfoListByBdUserId(Long bdUserId) {
		String hql = "from UserInfo where userId in(select userId from BdUserInvite where bdUserId=%s) and isAgent=1";
		return this.query(String.format(hql, bdUserId));
	}
	
	@Override
	public List<UserInfo> getUserInfoListByBdUserIdAndIsImportIm(Long bdUserId,Boolean isImportIm) {
		String hql = "from UserInfo where userId in(select userId from BdUserInvite where bdUserId=%s) and isAgent=1 and isImportIm=%s";
		return this.query(String.format(hql, bdUserId,isImportIm));
	}

	@Override
	public List<UserInfo> getUserInfoListByBdUserIdAndIsPortraitSet(Long bdUserId, Boolean isPortraitSet) {
		String hql = "from UserInfo where userId in(select userId from BdUserInvite where bdUserId=%s) and isAgent=1 and isImportIm=true and isPortraitSet=%s";
		return this.query(String.format(hql, bdUserId,isPortraitSet));
	}

	@Override
	public List<UserInfo> getUserInfoListByIsAutoImOperator(Boolean isAutoImOperator) {
		return null;
	}

	@Override
	public List<UserInfo> getUserInfoListByIsImAdviser(Boolean isImAdviser) {
		return null;
	}

	@Override
	public void updateUserInfoIsImportIm(Boolean isImportIm, List<Long> deleteUserIdList) {
	}
}
