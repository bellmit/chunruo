package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.HotSaleRecord;

@Repository("hotSaleRecordRepository")
public interface HotSaleRecordRepository extends GenericRepository<HotSaleRecord, Long> {

	@Query("from HotSaleRecord where updateTime >:updateTime")
	public List<HotSaleRecord> getHotSaleRecordListByUpdateTime(@Param("updateTime") Date updateTime);
}
