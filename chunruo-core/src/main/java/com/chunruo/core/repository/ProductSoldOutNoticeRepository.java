package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSoldOutNotice;

@Repository("productSoldOutNoticeRepository")
public interface ProductSoldOutNoticeRepository extends GenericRepository<ProductSoldOutNotice, Long> {

	@Query("from ProductSoldOutNotice where productId=:productId and productSpecId=:productSpecId and userId=:userId")
	ProductSoldOutNotice getProductSoldOutNoticeByProductIdAndUserId(@Param("productId")Long productId, @Param("productSpecId")Long productSpecId, @Param("userId")Long userId);

	@Query("from ProductSoldOutNotice where userId=:userId")
	List<ProductSoldOutNotice> getProductSoldOutNoticeListByUserId(@Param("userId")Long userId);

	@Query("from ProductSoldOutNotice where updateTime>:updateTime")
	List<ProductSoldOutNotice> getProductSoldOutNoticeListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from ProductSoldOutNotice where productId in(:idList) and isNotice=0")
	List<ProductSoldOutNotice> getProductSoldOutNoticeListByProductIdList(@Param("idList")List<Long> idList);
}
