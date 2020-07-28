package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.model.UserWithdrawalHistory;
import com.chunruo.core.repository.UserWithdrawalRepository;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserWithdrawalHistoryManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userWithdrawalManager")
public class UserWithdrawalManagerImpl extends GenericManagerImpl<UserWithdrawal, Long> implements UserWithdrawalManager {
	private Lock lock = new ReentrantLock();
	private UserWithdrawalRepository userWithdrawalRepository;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserWithdrawalHistoryManager userWithdrawalHistoryManager;
	@Autowired
	private UserAmountChangeRecordManager userAmountChangeRecordManager;

	@Autowired
	public UserWithdrawalManagerImpl(UserWithdrawalRepository userWithdrawalRepository) {
		super(userWithdrawalRepository);
		this.userWithdrawalRepository = userWithdrawalRepository;
	}

	@Override
	public List<UserWithdrawal> getUserWithdrawalListByUserId(Long userId) {
		return this.userWithdrawalRepository.getUserWithdrawalListByUserId(userId);
	}
	
	@Override
	public List<UserWithdrawal> getUserWithdrawalListByUpdateTime(Date updateTime){
		return this.userWithdrawalRepository.getUserWithdrawalListByUpdateTime(updateTime);
	}

	@Override
	public Double countUserWithdrawalTotalIncomeByUserId(Long userId) {
		Double totalInCome = 0.0D;
		String sql = "select sum(amount) from jkd_user_withdrawal where user_id = ? and status in(1,2,3)";
		List<Object[]> objectList = this.querySql(sql, new Object[]{userId});
		if(objectList != null && objectList.size() > 0){
			totalInCome = StringUtil.nullToDoubleFormat(objectList.get(0));
		}
		return totalInCome;
	}
	
	@Override
	public List<UserWithdrawal> getUserWithdrawalListByTime(Date beginDate, Date endDate, Integer status) {
		if(status == 5) {
			return this.userWithdrawalRepository.getUserWithdrawalListByTime(beginDate, endDate);
		}else {
		    return this.userWithdrawalRepository.getUserWithdrawalListByTime(beginDate, endDate, status);
		}
	}
	
	
	@Override
	public UserWithdrawal saveUserWithdrawal(UserWithdrawal userWithdrawal, Long userId, String title, String message) {
		// 加锁
		lock.lock();
		try{
			userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
			userWithdrawal = this.save(userWithdrawal);
			if(StringUtil.compareObject(UserWithdrawal.USER_WITHDRAWAL_STATUS_FAIL, userWithdrawal.getStatus())) {
				    //提现失败，返回金额到用户账户
			    	// 查询结算记录表是否为空
					List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userWithdrawal.getRecordId(),UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL_FAIL);
					if (CollectionUtils.isEmpty(recordList)) {
						UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(userWithdrawal.getUserId()));
					    if(userInfo != null && userInfo.getUserId() != null) {
							Double balance = StringUtil.nullToDoubleFormat(userInfo.getBalance());
					    	Double afterAmount = StringUtil.nullToDoubleFormat(DoubleUtil.add(balance,StringUtil.nullToDouble(userWithdrawal.getAmount())));

							Double afterWithdrawAmount = StringUtil.nullToDoubleFormat(DoubleUtil.sub(userInfo.getWithdrawalAmount(), userWithdrawal.getAmount()));

					    	userInfo.setBalance(afterAmount);
					    	userInfo.setWithdrawalAmount(afterWithdrawAmount);
					    	userInfo.setUpdateTime(DateUtil.getCurrentDate());
					    	this.userInfoManager.save(userInfo);
							// 店铺金额变动记录
							UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
							changeRecord.setUserId(userWithdrawal.getUserId());
							changeRecord.setObjectId(userWithdrawal.getRecordId());
							changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL_FAIL);
							changeRecord.setBeforeAmount(balance);
							changeRecord.setChangeAmount(userWithdrawal.getAmount());
							changeRecord.setAfterAmount(afterAmount);
							changeRecord.setCreateTime(DateUtil.getCurrentDate());
							changeRecord.setUpdateTime(changeRecord.getCreateTime());
							this.userAmountChangeRecordManager.save(changeRecord);
					}
			    }
			}
			//提现记录
			UserWithdrawalHistory storeWithdrawalHistory = new UserWithdrawalHistory ();
			storeWithdrawalHistory.setUserId(userId);
			storeWithdrawalHistory.setRecordId(userWithdrawal.getRecordId());
			storeWithdrawalHistory.setName(title);
			storeWithdrawalHistory.setMessage(message);
			storeWithdrawalHistory.setCreateTime(DateUtil.getCurrentDate());
			this.userWithdrawalHistoryManager.save(storeWithdrawalHistory);
			
			return userWithdrawal;
		}finally{
			// 释放锁
			lock.unlock();
		}
	}
	
	@Override
	public UserWithdrawal updateUserWithdrawal(UserWithdrawal userWithdrawal, Long userId, String title, String message) {
		// 加锁
		lock.lock();
		try{
			userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
			userWithdrawal = this.save(userWithdrawal);
			
			// 修改店铺银行信息
			UserInfo userInfo = this.userInfoManager.get(userWithdrawal.getUserId());
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			this.userInfoManager.update(userInfo);
			
			//提现记录
			UserWithdrawalHistory storeWithdrawalHistory = new UserWithdrawalHistory ();
			storeWithdrawalHistory.setUserId(userId);
			storeWithdrawalHistory.setRecordId(userWithdrawal.getRecordId());
			storeWithdrawalHistory.setName(title);
			storeWithdrawalHistory.setMessage(message);
			storeWithdrawalHistory.setCreateTime(DateUtil.getCurrentDate());
			this.userWithdrawalHistoryManager.save(storeWithdrawalHistory);
			return userWithdrawal;
		}finally{
			// 释放锁
			lock.unlock();
		}
	}
	
	@Override
	public MsgModel<Double> insertUserDrawalRecord(Map<String,Object> paramMap){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		msgModel.setIsSucc(true);
		if (paramMap != null && paramMap.size() > 0) {
			// 加锁
			lock.lock();
			try {
				UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(paramMap.get("userId")));
				if (userInfo == null || userInfo.getUserId() == null) {
					msgModel.setIsSucc(false);
					msgModel.setMessage("错误, 用户不存在");
					return msgModel;
				}

				// 检查提现金额是否有效
				Double amount = StringUtil.nullToDoubleFormat(StringUtil.nullToDouble(paramMap.get("amount")));
				Double balance = StringUtil.nullToDoubleFormat(userInfo.getBalance());
				if (amount.compareTo(balance) == 1) {
					// 提现金额大于店铺实际金额
					msgModel.setIsSucc(false);
					msgModel.setMessage("错误,提现金额不能大于可提现金额");
					return msgModel;
				}


				Double afterAmount = StringUtil.nullToDoubleFormat(DoubleUtil.sub(balance , amount));
				userInfo.setBalance(afterAmount);
				userInfo.setWithdrawalAmount(StringUtil.nullToDoubleFormat(DoubleUtil.add(StringUtil.nullToDouble(userInfo.getWithdrawalAmount()), amount)));
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				userInfo = this.userInfoManager.save(userInfo);

				// 提现记录
				UserWithdrawal userWithdrawal = new UserWithdrawal();
				userWithdrawal.setAmount(amount);
				userWithdrawal.setRemarks("提现");
				userWithdrawal.setStatus(Constants.WithdrawalStatus.NEW_STATUS);
				userWithdrawal.setTradeNo(CoreInitUtil.getRandomNo());
				userWithdrawal.setName(StringUtil.null2Str(paramMap.get("name")));
				userWithdrawal.setUserId(userInfo.getUserId());
				userWithdrawal.setCreateTime(DateUtil.getCurrentDate());
				userWithdrawal.setUpdateTime(userWithdrawal.getCreateTime());
				userWithdrawal = this.save(userWithdrawal);

				// 查询结算记录表是否为空
				List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userWithdrawal.getRecordId(),UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL);
				if (CollectionUtils.isEmpty(recordList)) {
					// 店铺金额变动记录
					UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
					changeRecord.setUserId(userWithdrawal.getUserId());
					changeRecord.setObjectId(userWithdrawal.getRecordId());
					changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL);
					changeRecord.setBeforeAmount(balance);
					changeRecord.setChangeAmount(amount);
					changeRecord.setAfterAmount(afterAmount);
					changeRecord.setCreateTime(DateUtil.getCurrentDate());
					changeRecord.setUpdateTime(changeRecord.getCreateTime());
					this.userAmountChangeRecordManager.save(changeRecord);
				}

				msgModel.setData(afterAmount);
				msgModel.setIsSucc(true);
				msgModel.setMessage("申请提现请求成功");
				return msgModel;
			} catch (Exception e) {
				e.printStackTrace();
				 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
			} finally {
				// 释放锁
				lock.unlock();
			}
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("错误,请求服务器异常");
		return msgModel;
	}

	/**
	 * 当月申请提现金额、已扣税费
	 */
	@Override
	public List<Object[]> countUserWithdrawalByIdCardNoCurrentMonth(String idCardNo) {
		String dateFormat = "%Y-%m";
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select sum(amount),sum(personal_tax) from jkd_user_withdrawal ");
		strBulSql.append("where type = 1 and status in(1,2,3) ");
		strBulSql.append("and date_format(create_time,'%s') = date_format(now(),'%s') ");
		strBulSql.append("and id_card_no = '%s' ");
		return this.querySql(String.format(strBulSql.toString(), dateFormat,dateFormat,idCardNo));
	}
	
}
