package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserFriend;

@Repository("userFriendRepository")
public interface UserFriendRepository extends GenericRepository<UserFriend, Long> {

	@Query("from UserFriend where friendUserId=:friendUserId")
	UserFriend getUserFriendByFriendUserId(@Param("friendUserId")Long friendUserId);

	@Query("from UserFriend where userId=:userId")
	List<UserFriend> getUserFriendListByUserId(@Param("userId")Long userId);

	@Query("from UserFriend where updateTime>:updateTime")
	List<UserFriend> getUserFriendListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from UserFriend where userId in(:userIdList)")
	List<UserFriend> getUserFriendListByUserIdList(@Param("userIdList")List<Long> userIdList);

	@Query("from UserFriend where userId =:userId and friendUserId in(:friendUserIdList)")
	List<UserFriend> getUserFriendByFriendUserIdAndFriendUserIdList(@Param("userId")Long userId, @Param("friendUserIdList")List<Long> friendUserIdList);

	@Modifying
	@Query("delete from UserFriend where friendUserId in(:friendUserIdList)")
	void deleteUserFriendByFriendUserIdList(@Param("friendUserIdList")List<Long> friendUserIdList);

	@Query("from UserFriend where  status=1 and friendUserId in(:friendUserIdList)")
	List<UserFriend> getUserFriendByFriendUserIdList(@Param("friendUserIdList")List<Long> friendUserIdList);

	@Query("from UserFriend where  status=:status")
	List<UserFriend> getUserFriendByStatus(@Param("status")Integer status);

	@Query("from UserFriend where  type=:type")
	List<UserFriend> getUserFriendByType(@Param("type")Integer type);

	@Modifying
	@Query("delete from UserFriend where userId =:userId")
	void deleteUserFriendByUserId(@Param("userId")Long userId);

}
