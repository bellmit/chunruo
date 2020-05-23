package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserInfo;

@Repository("userInfoRepository")
public interface UserInfoRepository extends GenericRepository<UserInfo, Long> {

	@Query("from UserInfo where mobile=:mobile and countryCode =:countryCode")
	public UserInfo getUserInfoByMobile(@Param("mobile") String mobile, @Param("countryCode") String countryCode);
	
	@Query("from UserInfo where updateTime >:updateTime ")
	public List<UserInfo> getUserInfoListByUpdateTime(@Param("updateTime") Date updateTime);
	
	@Query("from UserInfo where openId=:openId")
	public List<UserInfo> getUserInfoByOpenId(@Param("openId") String openId);
	
	@Query("from UserInfo where unionId=:unionId")
	public List<UserInfo> getUserInfoByUnionId(@Param("unionId") String unionId);
	
	@Query("from UserInfo where oldUnionId=:oldUnionId")
	public List<UserInfo> getUserInfoByOldUnionId(@Param("oldUnionId") String oldUnionId);
	
	@Query("from UserInfo where topUserId=:topUserId order by createTime desc")
	public List<UserInfo> getUserInfoByTopUserId(@Param("topUserId")Long topUserId);
	
	@Query("from UserInfo where topUserId in (:topUserId) order by createTime desc")
	public List<UserInfo> getUserInfoByTopUserIdList(@Param("topUserId")List<Long> topUserIdList);
	
	@Modifying
	@Query("update UserInfo set unionId =:unionId, oldUnionId=null, updateTime=now() where userId =:userId")
	public void updateUserInfo(@Param("unionId")String unionId ,@Param("userId") Long userId);
	
	@Modifying
	@Query("update UserInfo set headerImage =:headerImage where userId =:userId")
	public void updateUserHeaderImage(@Param("headerImage")String headerImage, @Param("userId") Long userId);
	
	@Query("from UserInfo where isAgent = true and status = true order by createTime desc")
	public List<UserInfo> getAllAgent();
	
	@Query("from UserInfo where isAgent = true and status = true and level=:level order by createTime desc")
	public List<UserInfo> getAgentUserByLevel(@Param("level") Integer level);
	
	@Modifying
	@Query("update UserInfo set mobile=:newMobile,updateTime=NOW() WHERE mobile=:oldMobile")
	public void editUserMobile(@Param("oldMobile") String oldMobile, @Param("newMobile") String newMobile);

	@Query("from UserInfo where topUserId =:topUserId and isAgent = true")
	public List<UserInfo> getUserInfoListByTopUserId(@Param("topUserId")Long topUserId);
	
	@Query("from UserInfo where inviterCode =:inviterCode")
	public List<UserInfo> getUserInfoListByInviterCode(@Param("inviterCode")String inviterCode);

	@Modifying
	@Query("update UserInfo set level=:level,updateTime=:updateTime where userId=:userId")
	public void updateUserLevel(@Param("userId") Long userId, @Param("level")Integer level, @Param("updateTime")Date updateTime);

	@Query("from UserInfo where isAgent = 1 and pushLevel in(:pushLevelList)")
	public List<UserInfo> getUserInfoListByPushLevelList(@Param("pushLevelList")List<Integer> pushLevelList);

	@Query("from UserInfo where isAgent = true and status = true and level in(2,4,5) order by createTime desc")
	public List<UserInfo> getDecleareUserList();

	@Query("from UserInfo where  userId in(:userIdList)")
	public List<UserInfo> getUserInfoListByUserIdList(@Param("userIdList")List<Long> userIdList);

	@Query("from UserInfo where  TIMESTAMPDIFF(MONTH,expireEndDate, DATE_FORMAT(NOW(), '%Y-%m-%d')) >= 2 and level = 1 and isAgent = 1 and  balance > 0 ")
	public List<UserInfo> getUserInfoListByTwoMonthExpire();

	@Query("from UserInfo where isBdUser = true and status=true  order by createTime desc")
	public List<UserInfo> getBdUserInfo();

	@Query("from UserInfo where isAgent = 1 and level in(:levelList)")
	public List<UserInfo> getUserInfoListByLevelList(@Param("levelList")List<Integer> levelList);




}
