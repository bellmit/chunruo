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
	public List<UserInviteRecord> getUserInviteRecordByUserId(Long userId) {
		return this.userInviteRecordRepository.getUserInviteRecordByUserId(userId);
	}

	@Override
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(Long topStoreId, Integer inviteType){
		return this.userInviteRecordRepository.getValidInvitNumberByTopUserId(topStoreId, inviteType);
	}

	@Override
	public MsgModel<Map<String, List<Long>>> insertInviteRecordByOrder(Order order){
		List<Long> userIdList = new ArrayList<Long>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		// 检查当前用户是否为代理
		UserInfo userInfo = this.userInfoManager.get(order.getUserId());
		if(userInfo != null && userInfo.getUserId() != null) {
			
			userInfo.setIsAgent(true);
			userInfo.setUpgradeTime(DateUtil.getCurrentDate());
			userInfo.setLevel(UserLevel.USER_LEVEL_DEALER);
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			userInfoList.add(userInfo);
			userIdList.add(userInfo.getUserId());
			
			
			List<UserInviteRecord> recordList = this.getUserInviteRecordByUserId(StringUtil.nullToLong(userInfo.getUserId()));
			if(recordList == null || recordList.isEmpty()){
				// 会员升级和续费记录
				UserInviteRecord inviteRecord = new UserInviteRecord ();
				inviteRecord.setRecordNo(CoreInitUtil.getRandomNo());		
				inviteRecord.setOrderNo(order.getOrderNo());			    
				inviteRecord.setUserId(userInfo.getUserId());				
				inviteRecord.setNumber(1D);                   	
				inviteRecord.setTopUserId(order.getTopUserId());		    
				inviteRecord.setIsPaymentSucc(true);						
				inviteRecord.setCostAmount(order.getOrderAmount());			
				inviteRecord.setProfitAmount(order.getProfitTop());			
				inviteRecord.setTradeNo(order.getTradeNo()); 				
				inviteRecord.setPaymentType(order.getPaymentType()); 		
				inviteRecord.setCreateTime(DateUtil.getCurrentDate());		
				inviteRecord.setUpdateTime(inviteRecord.getCreateTime());
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
			List<UserInviteRecord> recordList = this.getUserInviteRecordByUserId(userInfo.getUserId());
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
