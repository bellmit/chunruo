package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Sign;
import com.chunruo.core.model.SignRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.repository.SignRecordRepository;
import com.chunruo.core.service.SignManager;
import com.chunruo.core.service.SignRecordManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("signRecordManager")
public class SignRecordManagerImpl extends GenericManagerImpl<SignRecord, Long> implements SignRecordManager{
	private SignRecordRepository signRecordRepository;
	@Autowired
	private SignManager signManager;
	
	@Autowired
	public SignRecordManagerImpl(SignRecordRepository signRecordRepository) {
		super(signRecordRepository);
		this.signRecordRepository=signRecordRepository;
	}

	@Override
	public List<SignRecord> getSignRecordListByUserId(Long userId) {
		return this.signRecordRepository.getSignRecordListByUserId(userId);
	}

	@Override
	public void saveSignRecord(UserInfo userInfo,Integer isShare) {
		try {
			 //保存签到记录
		    SignRecord signRecord = new SignRecord();
		    signRecord.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
		    signRecord.setSignDate(DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getCurrentDate()));
		    signRecord.setCreateTime(DateUtil.getCurrentDate());
		    signRecord.setUpdateTime(DateUtil.getCurrentDate());
		    
		    Sign sign = this.signManager.getSignByUserId(StringUtil.nullToLong(userInfo.getUserId()));
		    if(sign != null && sign.getSignId() != null) {
		    	sign.setSignIntegral(StringUtil.nullToInteger(sign.getSignIntegral()) + 1);
		    	sign.setUpdateTime(DateUtil.getCurrentDate());
		    	if(StringUtil.compareObject(1, isShare)) {
		    		//分享签到
		    		sign.setShareCount(StringUtil.nullToInteger(sign.getShareCount()) + 1);
		    	}
		    }else {
		    	sign = new Sign();
		    	sign.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
		    	if(StringUtil.compareObject(1, isShare)) {
		    		//分享签到
		    		sign.setShareCount(1);
		    	}
		    	sign.setSignIntegral(1);
//		    	sign.setContinuedDays(1);
		    	sign.setCreateTime(DateUtil.getCurrentDate());
		    	sign.setUpdateTime(DateUtil.getCurrentDate());
		    }

		    this.save(signRecord);
		    this.signManager.save(sign);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<SignRecord> getSignRecordListByUpdateTime(Date updateTime) {
		return this.signRecordRepository.getSignRecordListByUpdateTime(updateTime);
	}

}
