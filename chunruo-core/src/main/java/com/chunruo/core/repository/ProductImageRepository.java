package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductImage;

@Repository("productImageRepository")
public interface ProductImageRepository extends GenericRepository<ProductImage, Long> {

	@Query("from ProductImage where productId=:productId and imageType=:imageType order by sort")
	public List<ProductImage> getProductImageListByProductId(@Param("productId")Long productId, @Param("imageType")Integer imageType);
	
	@Query("from ProductImage where updateTime >:updateTime")
	public List<ProductImage> getImageListByUpdateTime(@Param("updateTime") Date updateTime);
}
