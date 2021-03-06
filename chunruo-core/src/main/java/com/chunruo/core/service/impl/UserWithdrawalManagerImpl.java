package com.chunruo.core.service.impl;

import java.math.BigDecimal;
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
		String sql = "select sum(real_amount) from jkd_user_withdrawal where user_id = ? and status in(1,2,3)";
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
		// ??????
		lock.lock();
		try{
			userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
			userWithdrawal = this.save(userWithdrawal);
			if(StringUtil.compareObject(UserWithdrawal.USER_WITHDRAWAL_STATUS_FAIL, userWithdrawal.getStatus())) {
				    //??????????????????????????????????????????
			    	// ?????????????????????????????????
					List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userWithdrawal.getRecordId(),UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL_FAIL);
					if (CollectionUtils.isEmpty(recordList)) {
						UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(userWithdrawal.getUserId()));
					    if(userInfo != null && userInfo.getUserId() != null) {
							Double balance = StringUtil.nullToDoubleFormat(userInfo.getBalance());
					    	Double afterAmount = StringUtil.nullToDoubleFormat(DoubleUtil.add(balance,StringUtil.nullToDouble(userWithdrawal.getRealAmount())));

							Double afterWithdrawAmount = StringUtil.nullToDoubleFormat(DoubleUtil.sub(userInfo.getWithdrawalAmount(), userWithdrawal.getRealAmount()));

					    	userInfo.setBalance(afterAmount);
					    	userInfo.setWithdrawalAmount(afterWithdrawAmount);
					    	userInfo.setUpdateTime(DateUtil.getCurrentDate());
					    	this.userInfoManager.save(userInfo);
							// ????????????????????????
							UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
							changeRecord.setUserId(userWithdrawal.getUserId());
							changeRecord.setObjectId(userWithdrawal.getRecordId());
							changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL_FAIL);
							changeRecord.setBeforeAmount(balance);
							changeRecord.setChangeAmount(userWithdrawal.getRealAmount());
							changeRecord.setAfterAmount(afterAmount);
							changeRecord.setCreateTime(DateUtil.getCurrentDate());
							changeRecord.setUpdateTime(changeRecord.getCreateTime());
							this.userAmountChangeRecordManager.save(changeRecord);
					}
			    }
			}
			//????????????
			UserWithdrawalHistory storeWithdrawalHistory = new UserWithdrawalHistory ();
			storeWithdrawalHistory.setUserId(userId);
			storeWithdrawalHistory.setRecordId(userWithdrawal.getRecordId());
			storeWithdrawalHistory.setName(title);
			storeWithdrawalHistory.setMessage(message);
			storeWithdrawalHistory.setCreateTime(DateUtil.getCurrentDate());
			this.userWithdrawalHistoryManager.save(storeWithdrawalHistory);
			
			return userWithdrawal;
		}finally{
			// ?????????
			lock.unlock();
		}
	}
	
	@Override
	public UserWithdrawal updateUserWithdrawal(UserWithdrawal userWithdrawal, Long userId, String title, String message) {
		// ??????
		lock.lock();
		try{
			userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
			userWithdrawal = this.save(userWithdrawal);
			
			// ????????????????????????
			UserInfo userInfo = this.userInfoManager.get(userWithdrawal.getUserId());
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			this.userInfoManager.update(userInfo);
			
			//????????????
			UserWithdrawalHistory storeWithdrawalHistory = new UserWithdrawalHistory ();
			storeWithdrawalHistory.setUserId(userId);
			storeWithdrawalHistory.setRecordId(userWithdrawal.getRecordId());
			storeWithdrawalHistory.setName(title);
			storeWithdrawalHistory.setMessage(message);
			storeWithdrawalHistory.setCreateTime(DateUtil.getCurrentDate());
			this.userWithdrawalHistoryManager.save(storeWithdrawalHistory);
			return userWithdrawal;
		}finally{
			// ?????????
			lock.unlock();
		}
	}
	
	@Override
	public MsgModel<Double> insertUserDrawalRecord(Map<String,Object> paramMap){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		msgModel.setIsSucc(true);
		if (paramMap != null && paramMap.size() > 0) {
			// ??????
			lock.lock();
			try {
				UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(paramMap.get("userId")));
				if (userInfo == null || userInfo.getUserId() == null) {
					msgModel.setIsSucc(false);
					msgModel.setMessage("??????, ???????????????");
					return msgModel;
				}

				// ??????????????????????????????
				Double realAmount = StringUtil.nullToDoubleFormat(StringUtil.nullToDouble(paramMap.get("amount")));
				Double balance = StringUtil.nullToDoubleFormat(userInfo.getBalance());
				if (realAmount.compareTo(balance) == 1) {
					// ????????????????????????????????????
					msgModel.setIsSucc(false);
					msgModel.setMessage("??????,???????????????????????????????????????");
					return msgModel;
				}


				Double tax = DoubleUtil.mul(realAmount, 0.13D);
				Double amount = DoubleUtil.sub(realAmount, tax);
				Double afterAmount = StringUtil.nullToDoubleFormat(DoubleUtil.sub(balance , realAmount));
				userInfo.setBalance(afterAmount);
				userInfo.setWithdrawalAmount(StringUtil.nullToDoubleFormat(DoubleUtil.add(StringUtil.nullToDouble(userInfo.getWithdrawalAmount()), realAmount)));
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				userInfo = this.userInfoManager.save(userInfo);

				// ????????????
				UserWithdrawal userWithdrawal = new UserWithdrawal();
				userWithdrawal.setRealAmount(realAmount);
				userWithdrawal.setTax(tax);
				userWithdrawal.setAmount(amount);
				userWithdrawal.setRemarks("??????");
				userWithdrawal.setStatus(Constants.WithdrawalStatus.NEW_STATUS);
				userWithdrawal.setTradeNo(CoreInitUtil.getRandomNo());
				userWithdrawal.setName(StringUtil.null2Str(paramMap.get("name")));
				userWithdrawal.setUserId(userInfo.getUserId());
				userWithdrawal.setCreateTime(DateUtil.getCurrentDate());
				userWithdrawal.setUpdateTime(userWithdrawal.getCreateTime());
				userWithdrawal = this.save(userWithdrawal);

				// ?????????????????????????????????
				List<UserAmountChangeRecord> recordList = this.userAmountChangeRecordManager.getUserAmountChangeRecordByObjectId(userWithdrawal.getRecordId(),UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL);
				if (CollectionUtils.isEmpty(recordList)) {
					// ????????????????????????
					UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
					changeRecord.setUserId(userWithdrawal.getUserId());
					changeRecord.setObjectId(userWithdrawal.getRecordId());
					changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_DRAWAL);
					changeRecord.setBeforeAmount(balance);
					changeRecord.setChangeAmount(realAmount);
					changeRecord.setAfterAmount(afterAmount);
					changeRecord.setCreateTime(DateUtil.getCurrentDate());
					changeRecord.setUpdateTime(changeRecord.getCreateTime());
					this.userAmountChangeRecordManager.save(changeRecord);
				}

				msgModel.setData(afterAmount);
				msgModel.setIsSucc(true);
				msgModel.setMessage("????????????????????????");
				return msgModel;
			} catch (Exception e) {
				e.printStackTrace();
				 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //??????????????????
			} finally {
				// ?????????
				lock.unlock();
			}
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("??????,?????????????????????");
		return msgModel;
	}

	/**
	 * ???????????????????????????????????????
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
