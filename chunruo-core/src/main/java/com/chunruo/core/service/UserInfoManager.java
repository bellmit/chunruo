package com.chunruo.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.vo.TeamDataVo;

public interface UserInfoManager extends GenericManager<UserInfo, Long>{
	
	public UserInfo getUserInfoByMobile(String mobile, String countryCode);
	
	public UserInfo getUserInfoByOpenId(String openId);
	
	public boolean isExitsByMobile(String mobile, String countryCode);
	
	public UserInfo getUserInfoByUnionId(String unionId);
	
	public UserInfo getUserInfoByOldUnionId(String oldUnionId);
	
	public UserInfo saveAgent(int oauthType, Long topUserId, String mobile, String countryCode, UserSociety userSociety);
	
	public List<UserInfo> getUserInfoByTopUserIdList(List<Long> topUserIdList);
	
	/**
	 * 修改用户信息
	 * @param userId
	 * @param userSociety
	 * @return
	 */
	public UserInfo updateUserInfo(Long userId, UserSociety userSociety, boolean isDelUpdate);
	
	public void updateUserInfo(String unionId ,Long userId);

	public List<UserInfo> addAddress(List<UserInfo> userList);

	public void updateUserHeaderImage(String headerImage, Long userId);

	public List<UserInfo> getUserInfoListByUpdateTime(Date updateTime);
	
	public List<UserInfo> getAllAgent();

	public List<UserInfo> getAgentUserByLevel(Integer level);
	
	public List<UserInfo> getDecleareUserList();
	
	public Map<String,Integer> getDownLineCountByMobile(String mobile);
	
    public Integer getDownListCountByUserId(Long userId);
	
	public void editUserMobile(String oldMobile,String newMobile);

	public List<UserInfo> getSystemUserInfo();
	
	public List<UserInfo> getCustomerManagerUserInfo();

	public void setSystemUserInfo(List<Long> idList);
	
	public TeamDataVo getTeamDataInfo(Long userId);
	
	public List<String> getInveterCodeList(int size);
	
	public MsgModel<UserInfo> getTopUserInfoByTopUserId(Long topUserId);
	
	public MsgModel<UserInfo> checkUserNoStoreBuyAgent(Order order);
	
	public List<UserInfo> getUserInfoListByTopUserId(Long topUserId);
	
	public UserInfo getUserInfoByInviterCode(String inviterCode);

	public List<UserInfo> addTopStoreInfo(List<UserInfo> userList);

	public void updateUserInfoLevelByLoadFunction();
	
	public List<UserInfo> getUserInfoListByPushLevelList(List<Integer> pushLevelList);
	
	public Map<String,Long> getDownCountByUserIdList(List<Long> userIdList,String beginTime, String endTime);

	public void batchRegisterUserInfo(List<UserInfo> userInfoList, Long topUserId,MsgModel<Integer> msgModel);

	public List<UserInfo> getUserInfoListByUserIdList(List<Long> userIdList);

	public MsgModel<Void> updateTopUser(Long userId, String newMobile);

	public void insertUserV2ExpireEndDateByFunction();

	public void updateUserV2ToV1ByFunction();

	public UserInfo generateInviteCode(UserInfo userInfo);

	public void updateUserPushLevelByFunction();

	public void cleanBalance();
	
	public void updateUserInfoBalance(UserInfo userInfo);

	public List<UserInfo> getUserInfoListByIsImportIm(boolean isImportIm);

	public void updateUserInfoByIsImportIm(List<Long> userIdList, boolean isImportIm);

	public void updateUserInfoByIsPortraitSet(List<Long> userIdList, boolean isPortraitSet);
	
	public List<UserInfo> getOperatorUserInfo();

	public List<UserInfo> getBdUserInfo();

	public List<UserInfo> getUserInfoListByLevelList(List<Integer> levelList);

	public List<UserInfo> getUserInfoListByBdUserId(Long bdUserId);
	
	public List<UserInfo> getUserInfoListByBdUserIdAndIsImportIm(Long bdUserId,Boolean isImportIm);

	public List<UserInfo> getUserInfoListByBdUserIdAndIsPortraitSet(Long bdUserId, Boolean b);

	public List<UserInfo> getUserInfoListByIsAutoImOperator(Boolean isAutoImOperator);
	
	public List<UserInfo> getUserInfoListByIsImAdviser(Boolean isImAdviser);

	public void updateUserInfoIsImportIm(Boolean isImportIm, List<Long> deleteUserIdList);
}
