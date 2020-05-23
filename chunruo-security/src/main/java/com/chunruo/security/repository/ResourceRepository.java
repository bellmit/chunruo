package com.chunruo.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.chunruo.core.base.GenericRepository;
import com.chunruo.security.model.Resource;

/**
 * 访问权限
 * @author chunruo
 */
@Repository("resourceRepository")
public interface ResourceRepository extends GenericRepository<Resource, Long> {
	
	@Query("from Resource where upper(name)=:name")
	List<Resource> getResourceByName(@Param("name")String name);
	
	@Query("from Resource where linkPath=:linkPath")
	List<Resource> getResourceByLinkPath(@Param("linkPath")String linkPath);
	
	@Modifying
	@Query("update Resource set isEnable=:isEnable, updateTime=now() where resourceId in (:resourceIdList)")
	void updateEnable(@Param("resourceIdList")List<Long> resourceIdList, @Param("isEnable")boolean isEnable);
}
