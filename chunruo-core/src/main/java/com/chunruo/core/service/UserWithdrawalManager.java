package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.vo.MsgModel;

public interface UserWithdrawalManager extends GenericManager<UserWithdrawal, Long> {

	public List<UserWithdrawal> getUserWithdrawalListByUserId(Long userId);
	
	public List<UserWithdrawal> getUserWithdrawalListByUpdateTime(Date updateTime);
	
	public UserWithdrawal saveUserWithdrawal(UserWithdrawal userWithdrawal, Long userId, String title, String message);
	
	public UserWithdrawal updateUserWithdrawal(UserWithdrawal userWithdrawal, Long userId, String title, String message);

	public MsgModel<Double> insertUserDrawalRecord(Map<String,Object> paramMap);
	
	public List<UserWithdrawal> getUserWithdrawalListByTime(Date beginDate, Date endDate, Integer status);
	
	public Double countUserWithdrawalTotalIncomeByUserId(Long userId);

	public List<Object[]> countUserWithdrawalByIdCardNoCurrentMonth(String idCardNo);
}
