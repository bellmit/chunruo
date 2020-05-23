package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Keywords;

@Repository("keywordsRepository")
public interface KeywordsRepository extends GenericRepository<Keywords, Long>{
	
	@Query("from Keywords where isDefault =:isDefault")
	public List<Keywords> getKeywordsByIsDefault(@Param("isDefault") Boolean isDefault);
	
	@Modifying
	@Query("update Keywords k set k.isDefault =:isDefault where k.keywordsId =:keywordsId")
	public void updateKeywordsDefault(@Param("isDefault")boolean isDefault ,@Param("keywordsId") Long keywordsId);
}
