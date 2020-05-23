package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserAdviserTag;

@Repository("userAdviserTagRepository")
public interface UserAdviserTagRepository extends GenericRepository<UserAdviserTag, Long> {

	@Query("from UserAdviserTag where updateTime>:updateTime")
	public List<UserAdviserTag> getUserAdviserTagListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from UserAdviserTag where isEnable=:isEnable")
	public List<UserAdviserTag> getUserAdviserTagListByIsEnable(@Param("isEnable")Boolean isEnable);

	@Query("from UserAdviserTag where name=:name")
	public UserAdviserTag getUserAdviserTagByName(@Param("name")String name);
}
