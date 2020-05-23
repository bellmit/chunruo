package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.FxChildren;

@Repository("fxChildrenRepository")
public interface FxChildrenRepository extends GenericRepository<FxChildren, Long> {

	@Query("from FxChildren where pageId=:pageId order by sort asc")
    List<FxChildren> getFxChildrenListByPageId(@Param("pageId") Long pageId);
	
	@Modifying 
	@Query("delete from FxChildren p where p.pageId =:pageId")
    void deleteFxChildrenByPageId(@Param("pageId") Long pageId);
	
	@Modifying 
	@Query("delete from FxChildren p where p.pageId in (:pageIdList)")
    void deleteFxChildrenByPageIdList(@Param("pageIdList") List<Long> pageIdList);

	@Query("from FxChildren where pageId in(:pageIdList)")
	List<FxChildren> getFxChildrenListByPageIdList(@Param("pageIdList")List<Long> pageIdList);
}
