package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserInviteMember;

@Repository("userInviteMemberRepository")
public interface UserInviteMemberRepository extends GenericRepository<UserInviteMember, Long> {

	@Query("from UserInviteMember where userId=:userId")
	public UserInviteMember getUserInviteMemberByUserId(@Param("userId")Long userId);

	@Query("from UserInviteMember where updateTime>:updateTime")
	public List<UserInviteMember> getUserInviteMemberListByUpdateTime(@Param("updateTime")Date updateTime);

}
