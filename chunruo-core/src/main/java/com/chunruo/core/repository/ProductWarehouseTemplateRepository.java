package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductWarehouseTemplate;

@Repository("productWarehouseTemplateRepository")
public interface ProductWarehouseTemplateRepository extends GenericRepository<ProductWarehouseTemplate, Long> {

	@Query("from ProductWarehouseTemplate where status=:status")
	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByStatus(@Param("status")Boolean status);

	@Query("from ProductWarehouseTemplate where updateTime>:updateTime")
	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByUpdateTime(@Param("updateTime")Date updateTime);

	@Query("from ProductWarehouseTemplate where name=:name")
	public ProductWarehouseTemplate getMemberYearsTemplateByName(@Param("name")String name);


}
