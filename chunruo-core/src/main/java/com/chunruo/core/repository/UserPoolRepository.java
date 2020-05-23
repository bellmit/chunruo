package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserPool;

@Repository("userPoolRepository")
public interface UserPoolRepository extends GenericRepository<UserPool, Long> {

	@Query("from UserPool where userId=:userId")
	public UserPool getUserPoolByUserId(@Param("userId")Long userId);

	@Query("from UserPool where isBindBdUser=:isBindBdUser")
	public List<UserPool> getAllocatedUserPoolList(@Param("isBindBdUser")Boolean isBindBdUser);

	@Query("from UserPool where userId in(:userIdList) and isBindBdUser=:isBindBdUser")
	public List<UserPool> getUserPoolListByUserIdListAndIsBindBdUser(@Param("userIdList")List<Long> userIdList,@Param("isBindBdUser")Boolean isBindBdUser);

	@Query("from UserPool where userId in(:userIdList)")
	public List<UserPool> getUserPoolListByUserIdList(@Param("userIdList")List<Long> userIdList);

	@Transactional
	@Modifying
	@Query("delete from UserPool where userId in(:userIdList)")
	public void deleteUserPoolByUserIdList(@Param("userIdList")List<Long> userIdList);

}
