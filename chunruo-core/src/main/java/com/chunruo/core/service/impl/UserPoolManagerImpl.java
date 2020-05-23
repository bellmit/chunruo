package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserPool;
import com.chunruo.core.repository.UserPoolRepository;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserPoolManager;
import com.chunruo.core.vo.UserInfoVo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userPoolManager")
public class UserPoolManagerImpl extends GenericManagerImpl<UserPool, Long> implements UserPoolManager{
	private UserPoolRepository userPoolRepository;

	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	public UserPoolManagerImpl(UserPoolRepository userPoolRepository) {
		super(userPoolRepository);
		this.userPoolRepository = userPoolRepository;
	}

	@Override
	public UserPool getUserPoolByUserId(Long userId) {
		return this.userPoolRepository.getUserPoolByUserId(userId);
	}

	@Override
	public List<UserPool> getUserPoolListByType(Integer type,String keyword,Integer status) {
		
//		strBulSql.append("SELECT aa.number,jui.user_id,jui.level,jui.mobile,jui.nick_name,jui.register_time,jui.top_user_id FROM ");
//		strBulSql.append("(SELECT IFNULL(SUM(number),0) number,user_id FROM jkd_product_consultation_record jpcr  GROUP BY jpcr.user_id) aa ");
//		strBulSql.append("RIGHT JOIN jkd_user_info jui ON aa.user_id = jui.user_id ");
//		if(!StringUtil.isNull(keyword)) {
//			strBulSql.append("WHERE jui.mobile = '"+keyword+"' OR jui.user_id = "+keyword);
//		}
		
		Map<Long,UserInfoVo> numberMap = new HashMap<Long,UserInfoVo>();
		if(!StringUtil.isNull(keyword)) {
			StringBuilder strBulSql = new StringBuilder();
			strBulSql.append("SELECT jui.login_count,jui.user_id,jui.level,jui.mobile,jui.nick_name,jui.register_time,jui.top_user_id FROM jkd_user_info jui ");
			strBulSql.append("WHERE jui.mobile = '"+keyword+"' OR jui.user_id = "+keyword);
			List<Object[]> objectList = this.userInfoManager.querySql(strBulSql.toString());
			if(objectList != null && objectList.size() > 0) {
				for(Object[] object : objectList) {
					UserInfoVo userInfoVo = new UserInfoVo();
					userInfoVo.setNumber(StringUtil.nullToInteger(object[0]));
					userInfoVo.setUserId(StringUtil.nullToLong(object[1]));
					userInfoVo.setLevel(StringUtil.nullToInteger(object[2]));
					userInfoVo.setMobile(StringUtil.null2Str(object[3]));
					userInfoVo.setNickName(StringUtil.null2Str(object[4]));
					if(!StringUtil.isNull(StringUtil.null2Str(object[5]))) {
						userInfoVo.setRegisterTime(StringUtil.null2Str(object[5]).substring(0,19));
					}
					List<Long> specialUserIdList = StringUtil.stringToLongArray(StringUtil.null2Str(Constants.conf.getProperty("bd.special.invite.userId")));
					if(specialUserIdList.contains(StringUtil.nullToLong(object[6]))) {
						userInfoVo.setIsSpecialInvite(true);
					}
					numberMap.put(StringUtil.nullToLong(userInfoVo.getUserId()),userInfoVo);
				}
			}
		}
		
		
		List<UserPool> userPoolList = null;
		if (StringUtil.compareObject(type, UserPool.USER_POOL_TYPE3)) {
			// 已分配
			userPoolList =  this.userPoolRepository.getAllocatedUserPoolList(true);
		}else {
			userPoolList =  this.userPoolRepository.getAllocatedUserPoolList(false);
		}
		
		List<UserPool> currentUserPoolList = new ArrayList<UserPool>();
		if(userPoolList != null && userPoolList.size() > 0) {
			for(UserPool userPool : userPoolList) {
                if(!StringUtil.isNull(keyword) && !numberMap.containsKey(userPool.getUserId())) {
					continue;
				}
				if(StringUtil.compareObject(status, 0)
						&& StringUtil.compareObject(StringUtil.nullToInteger(userPool.getPoolType()), UserPool.USER_POOL_TYPE4)) {
					continue;
				}else if(StringUtil.compareObject(status, 1)
						&& !StringUtil.compareObject(StringUtil.nullToInteger(userPool.getPoolType()), UserPool.USER_POOL_TYPE4)) {
					continue;
				}
				
				currentUserPoolList.add(userPool);
				
				UserInfoVo userInfoVo = numberMap.get(StringUtil.nullToLong(userPool.getUserId()));
				if(userInfoVo == null || userInfoVo.getUserId() == null) {
					continue;
				}
				Integer number = StringUtil.nullToInteger(userInfoVo.getNumber());
				
//				if(status == 0) {
//					 if(StringUtil.compareObject(type, UserPool.USER_POOL_TYPE1)
//		                		&& number >= 2) {
//		                	continue;
//		             }else if(StringUtil.compareObject(type, UserPool.USER_POOL_TYPE2)
//		                		&& number < 2) {
//		                	continue;
//		             }
//				}
				
				userPool.setConsultationNumber(number);
				userPool.setIsSpecialInvite(StringUtil.nullToBoolean(userInfoVo.getIsSpecialInvite()));
				userPool.setLevel(StringUtil.nullToInteger(userInfoVo.getLevel()));
				userPool.setRegisterTime(userInfoVo.getRegisterTime());
				userPool.setMobile(StringUtil.null2Str(userInfoVo.getMobile()));
				userPool.setNickName(StringUtil.null2Str(userInfoVo.getNickName()));
				userPool.setEnterPoolTime(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, userPool.getCreateTime()));
			}
		}
		
		return currentUserPoolList;
	}

	@Override
	public List<UserPool> getUserPoolListByUserIdListAndIsBindBdUser(List<Long> userIdList,Boolean isBindBdUser) {
		if (userIdList != null && userIdList.size() > 0) {
			return this.userPoolRepository.getUserPoolListByUserIdListAndIsBindBdUser(userIdList,isBindBdUser);
		}
		return null;
	}

	@Override
	public List<UserPool> getUserPoolListByUserIdList(List<Long> userIdList) {
		if (userIdList != null && userIdList.size() > 0) {
			return this.userPoolRepository.getUserPoolListByUserIdList(userIdList);
		}
		return null;
	}

	@Override
	public void deleteUserPoolByUserIdList(List<Long> userIdList) {
		if(userIdList != null && userIdList.size() > 0) {
			this.userPoolRepository.deleteUserPoolByUserIdList(userIdList);
		}
	}
	
}
