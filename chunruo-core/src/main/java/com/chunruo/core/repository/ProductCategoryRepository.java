package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductCategory;

public interface ProductCategoryRepository extends GenericRepository<ProductCategory, Long> {
	
	@Query("from ProductCategory where level=:level and status=:status order by sort asc, categoryId")
	List<ProductCategory> getProductCategoryByLevel(@Param("level") int level, @Param("status")int status);

	@Query("from ProductCategory where status=:status order by sort asc, categoryId")
    List<ProductCategory> getProductCategoryByStatus(@Param("status") int status);
	
	@Query("from ProductCategory where parentId=:parentId and status=:status order by sort asc, categoryId")
    List<ProductCategory> getProductCategoryByParentId(@Param("parentId") Long parentId, @Param("status") int status);
	
	@Query("from ProductCategory where parentId in(:parentIdList) and status=:status order by sort asc")
    List<ProductCategory> getProductCategoryByLevel(@Param("parentIdList") List<Long> parentIdList, @Param("status") int status);

	@Modifying
	@Query("update ProductCategory set status =:status, updateTime = now() where categoryId in(:categoryIdList)")
	int updateProductCategoryStatus(@Param("categoryIdList") List<Long> categoryIdList, @Param("status") int status);

	@Query("from ProductCategory where parentId=:parentId and status=:status order by sort asc")
	List<ProductCategory> getProductCategory(@Param("parentId") Long parentId, @Param("status") int status);

	@Query("from ProductCategory where level=:level order by sort asc, categoryId")
	List<ProductCategory> getCategoryByLevel(@Param("level")int level);

	@Modifying
	@Query("update ProductCategory set status =:status, updateTime = now() where parentId=:parentId")
	int updateCategoryByParentId(@Param("parentId") Long parentId, @Param("status") int status);

	@Query("from ProductCategory where updateTime>:updateTime")
	List<ProductCategory> getProductCategoryListByUpdateTime( @Param("updateTime")Date updateTime);
}
