package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PageView;

@Repository("pageViewRepository")
public interface PageViewRepository extends GenericRepository<PageView, Long> {

	@Query("from PageView order by createTime desc")
	List<PageView> getPageViewListByCreateTime();

}
