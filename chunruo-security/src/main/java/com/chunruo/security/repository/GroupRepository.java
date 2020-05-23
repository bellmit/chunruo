package com.chunruo.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.chunruo.core.base.GenericRepository;
import com.chunruo.security.model.Group;

/**
 * 用户群组
 * @author chunruo
 */
@Repository("groupRepository")
public interface GroupRepository extends GenericRepository<Group, Long> {
	
	@Query("from Group where upper(name)=:name")
	List<Group> getGroupByName(@Param("name")String name);
	
	@Modifying
	@Query("update Group set isEnable=:isEnable, updateTime=now() where groupId in (:groupIdList)")
	void updateEnable(@Param("groupIdList")List<Long> groupIdList, @Param("isEnable")boolean isEnable);
}
