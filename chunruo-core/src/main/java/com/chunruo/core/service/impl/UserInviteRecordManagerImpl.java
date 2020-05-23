package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserInviteMember;
import com.chunruo.core.model.UserInviteRecord;
import com.chunruo.core.repository.UserInviteRecordRepository;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserInviteMemberManager;
import com.chunruo.core.service.UserInviteRecordManager;
import com.chunruo.core.service.UserTeamManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userInviteRecordManager")
public class UserInviteRecordManagerImpl extends GenericManagerImpl<UserInviteRecord, Long> implements UserInviteRecordManager{
	private UserInviteRecordRepository userInviteRecordRepository;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserTeamManager userTeamManager;
	@Autowired
	private UserInviteMemberManager userInviteMemberManager;

	@Autowired
	public UserInviteRecordManagerImpl(UserInviteRecordRepository userInviteRecordRepository) {
		super(userInviteRecordRepository);
		this.userInviteRecordRepository = userInviteRecordRepository;
	}

	@Override
	public UserInviteRecord getUserInviteRecordByRecordNo(String recordNo) {
		List<UserInviteRecord> list = this.userInviteRecordRepository.getUserInviteRecordByRecordNo(recordNo);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public List<UserInviteRecord> getUserInviteRecordByUserId(Long userId, Integer inviteType) {
		return this.userInviteRecordRepository.getUserInviteRecordByUserId(userId, inviteType);
	}

	@Override
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(Long topStoreId, Integer inviteType){
		return this.userInviteRecordRepository.getValidInvitNumberByTopUserId(topStoreId, inviteType);
	}

	@Override
	public MsgModel<Map<String, List<Long>>> insertInviteRecordByOrder(Order order){
		// 检查代理商续费
		String lastRenewEndDate = null;
		//默认普通或者店长升级
		int recordType = UserInviteRecord.RECORD_TYPE_FIRST; 

		// 检查当前用户是否为代理
		UserInfo userInfo = this.userInfoManager.get(order.getUserId());
		//购买大礼包总件数
		String dateFormat = DateUtil.DATE_FORMAT_YEAR;   
		Double packageQuantity = StringUtil.nullToDouble(order.getPackageYearNumber()); //大礼包年份
		if(StringUtil.nullToBoolean(userInfo.getIsAgent())){

			// 检查是否包含升级和续费记录
			List<UserInviteRecord> recordList = this.getUserInviteRecordByUserId(order.getUserId(), UserInviteRecord.VIP_TYPE_AGENT);
			if(recordList != null && recordList.size() > 0){
				for(UserInviteRecord inviteRecord : recordList){
					if(StringUtil.nullToBoolean(inviteRecord.getIsPaymentSucc())){
						// 本次充值为续费操作
						recordType = UserInviteRecord.RECORD_TYPE_RENEW;
						// 检查最后一次续费为有效日期
						if(DateUtil.isEffectiveTime(dateFormat, inviteRecord.getEndDate())
								|| DateUtil.isEffectiveTime(DateUtil.DATE_FORMAT_YEAR, inviteRecord.getEndDate())){
							if(lastRenewEndDate == null 
									|| StringUtil.null2Str(inviteRecord.getEndDate()).compareTo(lastRenewEndDate) > 0){
								// 找最大的截止时间
								lastRenewEndDate = inviteRecord.getEndDate();
							}
						}
						
						//经销商时间已过期，被降级为vip0
						if(StringUtil.compareObject(UserLevel.USER_LEVEL_BUYERS, StringUtil.nullToInteger(userInfo.getLevel()))) {
							//当前时间为开始时间
							lastRenewEndDate = DateUtil.formatDate(dateFormat, DateUtil.getCurrentDate());
						}
					}
				}
			}

			List<Integer> vipLevelList = new ArrayList<Integer>();
			vipLevelList.add(UserLevel.USER_LEVEL_DEALER);
			vipLevelList.add(UserLevel.USER_LEVEL_AGENT);
			vipLevelList.add(UserLevel.USER_LEVEL_V2);
			vipLevelList.add(UserLevel.USER_LEVEL_V3);
			if(vipLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
				// 本次充值为续费操作
				recordType = UserInviteRecord.RECORD_TYPE_RENEW;
				String defulatStartDate = DateUtil.formatDate(dateFormat, DateUtil.getCurrentDate());

				// 检查最后截止时间是否有效
				lastRenewEndDate = userInfo.getExpireEndDate();
				if(!DateUtil.isEffectiveTime(dateFormat, lastRenewEndDate)
						&& !DateUtil.isEffectiveTime(DateUtil.DATE_FORMAT_YEAR, lastRenewEndDate)){
					// 设置当前为开始时间
					lastRenewEndDate = defulatStartDate;
				}

				// 最后结算时间比当前时间要小,使用当前时间
				if(StringUtil.null2Str(defulatStartDate).compareTo(lastRenewEndDate) == 1){
					lastRenewEndDate = defulatStartDate;
				}
			}
		}

		String startDate = null;
		String endDate = null;
		List<UserInfo> userInfoList = new ArrayList<UserInfo> ();
		if(StringUtil.compareObject(recordType, UserInviteRecord.RECORD_TYPE_FIRST)){
			// 会员升级
			startDate = DateUtil.formatDate(dateFormat, DateUtil.getCurrentDate());
			endDate =DateUtil.formatDate(dateFormat, DateUtil.getYearAfterDay(DateUtil.getCurrentDate(),StringUtil.nullToDouble(Math.ceil(packageQuantity * 366)).intValue()));

			// 升级代理商
			userInfo.setIsAgent(true);
			userInfo.setLevel(UserLevel.USER_LEVEL_DEALER);
			userInfo.setExpireEndDate(endDate);
			this.userTeamManager.changeUserTeamRecord(userInfo);
		}else{
			// 会员续费
			Date lastRenewDate = DateUtil.parseDate(dateFormat, lastRenewEndDate);   
			startDate = DateUtil.formatDate(dateFormat, lastRenewDate);
			endDate =DateUtil.formatDate(dateFormat, DateUtil.getYearAfterDay(lastRenewDate,StringUtil.nullToDouble(Math.ceil(packageQuantity * 366)).intValue()));  //恢复

			// 代理商续费
			userInfo.setIsAgent(true);
			userInfo.setExpireEndDate(endDate);
			userInfo.setLevel(UserLevel.USER_LEVEL_DEALER);
		}
		
		// 当前用户更新时间
		userInfo.setUpdateTime(DateUtil.getCurrentDate());
		userInfoList.add(userInfo);

		// 缓存信息处理
		List<Long> userIdList = new ArrayList<Long> ();
		userIdList.add(userInfo.getUserId());

		// 会员升级和续费记录
		UserInviteRecord inviteRecord = new UserInviteRecord ();
		inviteRecord.setRecordNo(CoreInitUtil.getRandomNo());		//记录编号
		inviteRecord.setOrderNo(order.getOrderNo());			    //关联订单编号
		inviteRecord.setUserId(userInfo.getUserId());				//用户id
		inviteRecord.setNumber(packageQuantity);                   	//数量
		inviteRecord.setTopUserId(order.getTopUserId());		    //上级店铺id
		inviteRecord.setRecordType(recordType);						//记录类型 1-续费 2-首次购买
		inviteRecord.setInviteType(UserInviteRecord.VIP_TYPE_AGENT);//vip类型 1-普通代理 2-总代
		inviteRecord.setIsPaymentSucc(true);						//是否支付成功(false 未支付 true 已支付)
		inviteRecord.setCostAmount(order.getOrderAmount());			//费用金额
		inviteRecord.setProfitAmount(order.getProfitTop());			//返利金额
		inviteRecord.setTradeNo(order.getTradeNo()); 				//商家交易流水号(纯若)
		inviteRecord.setPaymentType(order.getPaymentType()); 		//支付方式(0:微信支付;1:支付宝支付)
		inviteRecord.setStartDate(startDate);						//有效期开始时间
		inviteRecord.setEndDate(endDate);							//有效期结束时间
		inviteRecord.setCreateTime(DateUtil.getCurrentDate());		//创建时间
		inviteRecord.setUpdateTime(inviteRecord.getCreateTime());//更新时间
		this.save(inviteRecord);

		// 批量保存变更用户信息
		userInfoList = this.userInfoManager.batchInsert(userInfoList, userInfoList.size());
		if(userInfoList != null && userInfoList.size() > 0){
			for(UserInfo user : userInfoList){
				if(!userIdList.contains(user.getUserId())){
					userIdList.add(user.getUserId());
				}
			}
		}
		

		MsgModel<Map<String, List<Long>>> msgModel = new MsgModel<Map<String, List<Long>>> ();
		Map<String, List<Long>> resultMap = new HashMap<String, List<Long>> ();
		resultMap.put("userIdList", userIdList);
		msgModel.setIsSucc(true);
		msgModel.setData(resultMap);
		return msgModel;

	}

	@Override
	public MsgModel<String> insertBuyInviteRecord(UserInfo userInfo){
		MsgModel<String> msgModel = new MsgModel<String> ();

		// 检查邀请人
		UserInfo topUserInfo = this.userInfoManager.get(userInfo.getUserId());
		if(topUserInfo != null 
				&& topUserInfo.getUserId() != null
				&& StringUtil.nullToBoolean(topUserInfo.getIsAgent()) 
				&& StringUtil.compareObject(StringUtil.nullToInteger(topUserInfo.getLevel()), UserLevel.USER_LEVEL_DEALER)){
			// 注册店长记录
			List<UserInviteRecord> recordList = this.getUserInviteRecordByUserId(userInfo.getUserId(), UserInviteRecord.VIP_TYPE_BUYERS);
			if(recordList != null && recordList.size() > 0){
				// 已存在错误
				msgModel.setMessage("店长已存在错误");
				msgModel.setIsSucc(false);
				return msgModel;
			}

			// 当前有效日期
			String currentDate = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate());
			UserInviteRecord inviteRecord = new UserInviteRecord ();
			inviteRecord.setRecordNo(CoreInitUtil.getRandomNo());	      		//记录编号
			inviteRecord.setOrderNo(null);							      		//关联订单编号
			inviteRecord.setUserId(userInfo.getUserId());				  		//用户id
			inviteRecord.setTopUserId(StringUtil.nullToLong(userInfo.getTopUserId()));		  			//上级店铺id
			inviteRecord.setRecordType(UserInviteRecord.RECORD_TYPE_FIRST); 		//记录类型 2-首次购买
			inviteRecord.setInviteType(UserInviteRecord.VIP_TYPE_BUYERS);   		//vip类型 3-店长
			inviteRecord.setIsPaymentSucc(false);						  		//是否支付成功(false 未支付 true 已支付)
			inviteRecord.setCostAmount(0D);		 					  			//费用金额
			inviteRecord.setProfitAmount(0D);		  					  		//返利金额
			inviteRecord.setTradeNo(null); 							  			//商家交易流水号(纯若)
			inviteRecord.setPaymentType(0); 		 				  	  			//支付方式(0:微信支付;1:支付宝支付)
			inviteRecord.setStartDate(currentDate);								//有效期开始时间
			inviteRecord.setEndDate(currentDate);	 							//有效期结束时间
			inviteRecord.setCreateTime(DateUtil.getCurrentDate());		  		//创建时间
			inviteRecord.setUpdateTime(inviteRecord.getCreateTime());	  	//更新时间
			inviteRecord = this.save(inviteRecord);
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("未知异常");
		return msgModel;
	}

	@Override
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(Long topUserId, Integer inviteType, Date beginDate, Date endDate) {
		return this.userInviteRecordRepository.getValidInvitNumberByTopUserId(topUserId, inviteType, beginDate, endDate);
	}
	
	@Override
	public List<UserInviteRecord> getUserInviteRecordListByCreateTime(Date beginDate, Date endDate) {
		return this.userInviteRecordRepository.getUserInviteRecordListByCreateTime(beginDate,endDate);
	}


	@Override
	public List<UserInviteRecord> getUserInviteRecordListByCurrentMonth() {
		return this.userInviteRecordRepository.getUserInviteRecordListByCurrentMonth();
	}

	@Override
	public List<UserInviteRecord> getUserInviteRecordList() {
		return this.userInviteRecordRepository.getUserInviteRecordList();
	}

	@Override
	public MsgModel<Map<String, List<Long>>> insertInviteRecordByIsFreeMemberOrder(Order order) {
		MsgModel<Map<String, List<Long>>> msgModel = new MsgModel<Map<String, List<Long>>>();
		Map<String, List<Long>> resultMap = new HashMap<String, List<Long>> ();
		if(StringUtil.nullToBoolean(order.getIsTemporaryMember())) {
			UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(order.getUserId()));
		    if(userInfo != null && userInfo.getUserId() != null
		    		&& StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_BUYERS)) {
		    	//更改用户等级
		    	userInfo.setLevel(UserLevel.USER_LEVEL_DEALER);
		    	String expireEndDate = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getDateAfterByDay(DateUtil.getCurrentDate(), 5));
		    	userInfo.setExpireEndDate(expireEndDate);
		    	userInfo.setUpdateTime(DateUtil.getCurrentDate());
		    	this.userInfoManager.save(userInfo);
		    	
		    	//保存试用经销商记录
		    	UserInviteMember userInviteMember = new UserInviteMember();
		    	userInviteMember.setOrderId(StringUtil.nullToLong(order.getOrderId()));
		    	userInviteMember.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
		    	userInviteMember.setIsDowngrade(false);
		    	userInviteMember.setEndTime(DateUtil.getDateAfterByDay(DateUtil.getCurrentDate(), 5));
		    	userInviteMember.setCreateTime(DateUtil.getCurrentDate());
		    	userInviteMember.setUpdateTime(userInviteMember.getCreateTime());
                this.userInviteMemberManager.save(userInviteMember);
                List<Long> userIdList = new ArrayList<Long>();
                userIdList.add(StringUtil.nullToLong(userInfo.getUserId()));
        		resultMap.put("userIdList", userIdList);
		    }
		}
		msgModel.setIsSucc(true);
		msgModel.setData(resultMap);
		return msgModel;
	}

	@Override
	public List<UserInviteRecord> getUserInviteRecordListByUserIdList(List<Long> userIdList) {
		if(userIdList == null || userIdList.size() <= 0) {
			return null;
		}
		return this.userInviteRecordRepository.getUserInviteRecordListByUserIdList(userIdList);
	}
}
