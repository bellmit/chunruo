package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PostageConfig;

@Repository("postageConfigRepository")
public interface PostageConfigRepository extends GenericRepository<PostageConfig, Long> {

	@Query("from PostageConfig where updateTime>:updateTime")
	List<PostageConfig> getPostageConfigListByUpdateTime(@Param("updateTime")Date updateTime);

}
