package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductSeckillNotice;

@Repository("productSeckillNoticeRepository")
public interface ProductSeckillNoticeRepository extends GenericRepository<ProductSeckillNotice, Long> {
	
	@Query("from ProductSeckillNotice where seckillId =:seckillId and userId =:userId and productId=:productId")
	public List<ProductSeckillNotice> getProductSeckillNoticeByUnique(@Param("seckillId") Long seckillId, @Param("userId") Long userId, @Param("productId") Long productId);
	
	@Query("from ProductSeckillNotice where noticeTime <=:noticeTime")
	public List<ProductSeckillNotice> getProductSeckillNoticeListByNoticeTime(@Param("noticeTime")Date noticeTime);

	@Query("from ProductSeckillNotice where seckillId =:seckillId")
	public List<ProductSeckillNotice> getProductSeckillNoticeListBySeckillId(@Param("seckillId")Long seckillId);

	@Query("from ProductSeckillNotice where userId =:userId")
	public List<ProductSeckillNotice> getProductSeckillNoticeListByUserId(@Param("userId")Long userId);

	@Query("from ProductSeckillNotice where updateTime >:updateTime")
	public List<ProductSeckillNotice> getProductSeckillNoticeListByUpdateTime(@Param("updateTime")Date updateTime);
}
