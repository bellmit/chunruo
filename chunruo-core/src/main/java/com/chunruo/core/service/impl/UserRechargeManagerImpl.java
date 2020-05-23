package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserRecharge;
import com.chunruo.core.repository.UserRechargeRepository;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserRechargeManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userRechargeManager")
public class UserRechargeManagerImpl extends GenericManagerImpl<UserRecharge, Long> implements UserRechargeManager {
	private UserRechargeRepository userRechargeRepository;
	
	@Autowired
	private UserInfoManager userInfoManager;

	@Autowired
	private UserAmountChangeRecordManager userAmountChangeRecordManager;
	
	@Autowired
	public UserRechargeManagerImpl(UserRechargeRepository userRechargeRepository) {
		super(userRechargeRepository);
		this.userRechargeRepository = userRechargeRepository;
	}

	@Override
	public void saveUserRecharge(UserRecharge userRecharge, List<UserInfo> userInfoList) {
		if(userInfoList != null && userInfoList.size() > 0
				&& userRecharge != null) {
			List<UserRecharge> userRechargeList = new ArrayList<UserRecharge>();
			for(UserInfo userInfo : userInfoList) {
				UserRecharge recharge = userRecharge.clone();
				recharge.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
				recharge.setCreateTime(DateUtil.getCurrentDate());
				recharge.setUpdateTime(DateUtil.getCurrentDate());
				userRechargeList.add(recharge);
			}
			if(userRechargeList != null && userRechargeList.size() > 0 ) {
				this.batchInsert(userRechargeList, userRechargeList.size());
			}
		}
		
	}

	@Override
	public void updateUserRechargeStatus(List<Long> idList, Integer status, Integer level,String refuseReason,String adminName) {
		if (idList != null && idList.size() > 0) {
			if (StringUtil.compareObject(level, 3)
					&& StringUtil.compareObject(status, UserRecharge.USER_RECHARGE_SUCC)) {
				// 财务审核同意时需确认充值状态
				List<UserRecharge> userRechargeList = this.userRechargeRepository.getByIdList(idList);
				if (userRechargeList != null && userRechargeList.size() > 0) {
					
					List<UserAmountChangeRecord> changeRecordList = new ArrayList<UserAmountChangeRecord>();
					for (UserRecharge userRecharge : userRechargeList) {
						if (!StringUtil.compareObject(userRecharge.getStatus(),UserRecharge.USER_RECHARGE_FIANCE)) {
							continue;
						}

						userRecharge.setStatus(UserRecharge.USER_RECHARGE_SUCC);
						userRecharge.setCompleteTime(DateUtil.getCurrentDate());
						userRecharge.setUpdateTime(DateUtil.getCurrentDate());
						Double amount = StringUtil.nullToDouble(userRecharge.getAmount());

						UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(userRecharge.getUserId()));
						// 待结算利润
						List<Object[]> profitRecordList = new ArrayList<Object[]>();
						profitRecordList.add(new Object[] { amount, amount, userInfo.getUserId() });

						// 店铺金额变动记录
						Double balance = StringUtil.nullToDouble(userInfo.getBalance());
						UserAmountChangeRecord changeRecord = new UserAmountChangeRecord();
						changeRecord.setUserId(userInfo.getUserId());
						changeRecord.setObjectId(userRecharge.getRecordId());
						changeRecord.setType(UserAmountChangeRecord.AMOUNT_CHANGE_RECHARGE);
						changeRecord.setBeforeAmount(balance);
						changeRecord.setChangeAmount(amount);
						changeRecord.setAfterAmount(balance + amount);
						changeRecord.setCreateTime(DateUtil.getCurrentDate());
						changeRecord.setUpdateTime(changeRecord.getCreateTime());
						changeRecordList.add(changeRecord);

						// 批量修改用户账户
						if (!CollectionUtils.isEmpty(profitRecordList)) {
							this.batchInsert(userRechargeList, userRechargeList.size());
							this.userAmountChangeRecordManager.batchInsert(changeRecordList,changeRecordList.size());
							this.batchUpdate("update jkd_user_info set balance=truncate(ifnull(balance,0) + ?, 2), income=truncate(ifnull(income,0) + ?, 2), update_time=now() where user_id=?",profitRecordList);
						}
					}
					
				}
			}else {
				//其他情况直接修改状态
				this.userRechargeRepository.updateUserRechargeStatus(idList, status,refuseReason,adminName);
			}
		}
	}

	@Override
	public List<UserRecharge> getUserRechargeListByUserIdAndStatus(Long userId,Integer status) {
		return this.userRechargeRepository.getUserRechargeListByUserIdAndStatus(userId,status);
	}

	@Override
	public List<UserRecharge> getUserRechargeListByUpdateTime(Date updateTime) {
		return this.userRechargeRepository.getUserRechargeListByUpdateTime(updateTime);
	}
	
	
	@Override
	public Double countUserRechargeTotalAmountByUserId(Long userId) {
		Double totalAmount = 0.0D;
		String sql = "select sum(amount) from jkd_user_recharge where user_id = ? and status = ?";
		List<Object[]> objectList = this.querySql(sql, new Object[]{userId,UserRecharge.USER_RECHARGE_SUCC});
		if(objectList != null && objectList.size() > 0){
			totalAmount = StringUtil.nullToDoubleFormat(objectList.get(0));
		}
		return totalAmount;
	}
	
}
