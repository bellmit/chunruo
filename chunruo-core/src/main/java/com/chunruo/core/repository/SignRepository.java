package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Sign;

@Repository("signRepository")
public interface SignRepository extends GenericRepository<Sign, Long> {

	@Query("from Sign where userId=:userId")
	Sign getSignByUserId(@Param("userId") Long userId);

	@Query("from Sign where updateTime>:updateTime")
	List<Sign> getSignListByUpdateTime(@Param("updateTime")Date updateTime);

}
