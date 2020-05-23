package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserFriendTag;

@Repository("userFriendTagRepository")
public interface UserFriendTagRepository extends GenericRepository<UserFriendTag, Long> {

	@Modifying
	@Query("delete from UserFriendTag where  tagId=:tagId")
	void deleteUserFriendTagByTagId( @Param("tagId")Long tagId);

	@Query("from UserFriendTag where tagId=:tagId")
	List<UserFriendTag> getUserFriendTagListByTagId(@Param("tagId")Long tagId);

	@Query("from UserFriendTag where updateTime>:updateTime")
	List<UserFriendTag> getUserFriendTagListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from UserFriendTag where userId=:userId")
	List<UserFriendTag> getUserFriendTagListByUserId(@Param("userId")Long userId);

	@Modifying
	@Query("delete from UserFriendTag where imUserId=:imUserId and userId in(:userIdList)")
	void deleteUserFriendTagByUserIdListAndImUserId(@Param("userIdList")List<Long> userIdList, @Param("imUserId")Long imUserId);

	@Modifying
	@Query("delete from UserFriendTag where userId=:userId")
	void deleteUserFriendTagByUserId(@Param("userId")Long userId);

	@Query("from UserFriendTag where imUserId=:imUserId")
	List<UserFriendTag> getUserFriendTagListByImUserId(@Param("imUserId")Long imUserId);

	@Modifying
	@Query("delete from UserFriendTag where tagId=:tagId and imUserId=:imUserId")
	void deleteUserFriendTagByTagIdAndImUserId(@Param("tagId")Long tagId, @Param("imUserId")Long imUserId);

	@Query("from UserFriendTag where userId in(:userIdList)")
	List<UserFriendTag> getUserFriendTagListByUserIdList(@Param("userIdList")List<Long> userIdList);

	@Modifying
	@Query("delete from UserFriendTag where userId in(:userIdList)")
	void deleteUserFriendTagByUserId(@Param("userIdList")List<Long> userIdList);

	@Query("from UserFriendTag where tagId in(:tagIdList)")
	List<UserFriendTag>  getUserFriendTagListByTagIdList(@Param("tagIdList")List<Long> tagIdList);


}
