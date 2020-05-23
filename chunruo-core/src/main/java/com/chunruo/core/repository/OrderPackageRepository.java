package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderPackage;

@Repository("orderPackageRepository")
public interface OrderPackageRepository extends GenericRepository<OrderPackage, Long> {
	
	@Query("from OrderPackage where updateTime >:updateTime")
	public List<OrderPackage> getOrderPackageListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Query("from OrderPackage where orderId =:orderId order by createTime desc")
	public List<OrderPackage> getOrderPackageListByOrderId(@Param("orderId")Long orderId);
	
	@Query("from OrderPackage where orderId in (:orderIdList) order by createTime desc")
	public List<OrderPackage> getOrderPackageListByOrderIdList(@Param("orderIdList") List<Long> orderIdList);
	
	@Query("from OrderPackage where orderId =:orderId and isHandler =:isHandler order by createTime desc")
	public List<OrderPackage> getOrderPackageListByOrderId(@Param("orderId")Long orderId, @Param("isHandler")Boolean isHandler);

}
