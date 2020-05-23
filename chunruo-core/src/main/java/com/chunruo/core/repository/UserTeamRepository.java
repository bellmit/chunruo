package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserTeam;

@Repository("userTeamRepository")
public interface UserTeamRepository extends GenericRepository<UserTeam, Long> {

	@Query("from UserTeam where updateTime >:updateTime")
	public List<UserTeam> getUserTeamListByUpdateTime(@Param("updateTime") Date updateTime);

	@Query("from UserTeam where topUserId =:topUserId")
	public List<UserTeam> getUserTeamListByTopUserId(@Param("topUserId")Long topUserId);

	@Query("from UserTeam where userId =:userId")
	public UserTeam getUserTeamByUserId(@Param("userId")Long userId);

	@Query("from UserTeam where userId in(:userIdList)")
	public List<UserTeam> getUserTeamListByUserIdList(@Param("userIdList")List<Long> userIdList);
}
