package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.repository.UserSocietyRepository;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userSocietyManager")
public class UserSocietyManagerImpl extends GenericManagerImpl<UserSociety, Long> implements UserSocietyManager{
	private UserSocietyRepository userSocietyRepository;
	@Autowired
	private UserInfoManager userInfoManager;
	
	@Autowired
	public UserSocietyManagerImpl(UserSocietyRepository userSocietyRepository) {
		super(userSocietyRepository);
		this.userSocietyRepository = userSocietyRepository;
	}
	
	@Override
	public List<UserSociety> getUserSocietyByUnionId(String unionId) {
		return this.userSocietyRepository.getUserSocietyByUnionId(unionId);
	}

	@Override
	public UserSociety getUserSocietyByOpenIdAndConfigId(Long appConfigId, String openId) {
		UserSociety userSociety = this.userSocietyRepository.getUserSocietyByOpenIdAndConfigId(appConfigId, openId);
		if(userSociety != null && userSociety.getUserSocietyId() != null){
			UserInfo userInfo = this.userInfoManager.getUserInfoByOpenId(userSociety.getOpenId());
			if(userInfo != null && userInfo.getUserId() != null){
				userSociety.setUserId(userInfo.getUserId());
				userSociety.setUserInfo(userInfo);
				userSociety.setUpdateTime(DateUtil.getCurrentDate());
				return this.save(userSociety);
			}
		}
		return userSociety;
	}
	
	@Override
	public UserSociety saveUserSociety(UserSociety userSociety) {
		userSociety.setUpdateTime(DateUtil.getCurrentDate());
		userSociety = this.save(userSociety);
		if(userSociety != null && userSociety.getUserSocietyId() != null){
			UserInfo userInfo = this.userInfoManager.getUserInfoByOpenId(userSociety.getOpenId());
			if (userInfo == null || userInfo.getUserId() == null){
				userInfo = new UserInfo ();
				userInfo.setUnionId(StringUtil.null2Str(userSociety.getUnionId()));			//???????????????????????????????????????????????????????????????????????????????????????
				userInfo.setIsAgent(true);
				userInfo.setLevel(UserLevel.USER_LEVEL_BUYERS);
				userInfo.setCountryCode(UserInfo.DEFUALT_COUNTRY_CODE);
				userInfo.setNickname(StringUtil.null2Str(userSociety.getNickname()));		//??????
				userInfo.setRegisterIp(StringUtil.null2Str(userSociety.getClientIp()));		//??????IP??????
				userInfo.setLastIp(StringUtil.null2Str(userSociety.getClientIp()));			//????????????IP??????
				userInfo.setLoginCount(0);													//????????????
				userInfo.setStatus(true);													//??????????????????
				userInfo.setHeaderImage(StringUtil.null2Str(userSociety.getHeadImgUrl()));	//????????????
				userInfo.setSex(StringUtil.nullToInteger(userSociety.getSex()));			//??????
				userInfo.setTopUserId(0L);
				userInfo.setOpenId(userSociety.getOpenId());
				userInfo.setCreateTime(DateUtil.getCurrentDate());		//????????????
				userInfo.setUpdateTime(userInfo.getCreateTime());		//????????????
				userInfo.setLastLoginTime(userInfo.getCreateTime());
				userInfo = this.userInfoManager.save(userInfo);
				if(userInfo != null && userInfo.getUserId() != null){
					userSociety.setUserId(userInfo.getUserId());
					userSociety.setUserInfo(userInfo);
				}
			}else{
				userSociety.setUserId(userInfo.getUserId());
				userSociety.setUserInfo(userInfo);
			}
		}
		return userSociety;
	}

	@Override
	public void deleteUserSocietyByOpenId(List<String> openidList, List<Long> userIdList) {
		this.userInfoManager.deleteByIdList(userIdList);
		this.userSocietyRepository.deleteUserSocietyByOpenId(openidList);
	}

	@Override
	public void deleteUserSocietyByUnionId(String unionId) {
		this.userSocietyRepository.deleteUserSocietyByUnionId(unionId);
	}

	@Override
	public UserSociety getUserSocietyByUniodIdAndAppConfigId(String unionId, Long appConfigId) {
		List<UserSociety> userSocietyList = this.userSocietyRepository.getUserSocietyByUniodIdAndAppConfigId(unionId,appConfigId);
		return (userSocietyList != null && userSocietyList.size() > 0) ? userSocietyList.get(0) : null;
	}
}
