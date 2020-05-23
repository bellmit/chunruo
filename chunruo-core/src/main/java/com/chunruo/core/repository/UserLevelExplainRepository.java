package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserLevelExplain;

@Repository("userLevelExplainRepository")
public interface UserLevelExplainRepository extends GenericRepository<UserLevelExplain, Long> {

	@Query("from UserLevelExplain where  level=:level and type in (:typeList) order by type, sort asc")
	public List<UserLevelExplain> getUserLevelExplainList(@Param("level") Integer level, @Param("typeList") List<Integer> typeList);

}
