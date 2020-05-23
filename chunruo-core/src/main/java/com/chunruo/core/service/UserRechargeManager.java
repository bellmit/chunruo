package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserRecharge;
public interface UserRechargeManager extends GenericManager<UserRecharge, Long>{

	
	public void saveUserRecharge(UserRecharge userRecharge, List<UserInfo> userInfoList);

	public void updateUserRechargeStatus(List<Long> idList, Integer status, Integer level,String refuseReason,String adminName);

	public List<UserRecharge> getUserRechargeListByUserIdAndStatus(Long userId,Integer status);

	public List<UserRecharge> getUserRechargeListByUpdateTime(Date updateTime);

	public Double countUserRechargeTotalAmountByUserId(Long userId);


	
}
