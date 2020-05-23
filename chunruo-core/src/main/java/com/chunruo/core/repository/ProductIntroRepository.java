package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductIntro;

@Repository("productIntroRepository")
public interface ProductIntroRepository extends GenericRepository<ProductIntro, Long> {

	@Query("from ProductIntro where updateTime >:updateTime")
	List<ProductIntro> getProductIntroListByUpdateTime(@Param("updateTime")Date updateTime);

}
