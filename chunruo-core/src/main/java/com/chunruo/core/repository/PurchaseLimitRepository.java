package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.PurchaseLimit;

@Repository("purchaseLimitRepository")
public interface PurchaseLimitRepository extends GenericRepository<PurchaseLimit, Long> {

	@Query("from PurchaseLimit where isEnable=:isEnable and isDelete=false order by createTime desc")
	public List<PurchaseLimit> getPurchaseLimitListByIsEnable(@Param("isEnable")boolean isEnable);

	@Query("from PurchaseLimit where updateTime>:updateTime")
	public List<PurchaseLimit> getPurchaseLimitRecordListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from PurchaseLimit where type=:type and isEnable=:isEnable and isDelete=false ")
	public List<PurchaseLimit> getPurchaseLimitListByTypeAndIsEnable(@Param("type")Integer type, @Param("isEnable")boolean isEnable);

	@Query("from PurchaseLimit where productId=:productId and isEnable=true ")
	public List<PurchaseLimit> getPurchaseLimitListProductId(@Param("productId")Long productId);

}
