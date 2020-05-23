package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserAuthOrder;

@Repository("userAuthOrderRepository")
public interface UserAuthOrderRepository extends GenericRepository<UserAuthOrder, Long> {

	@Query("from UserAuthOrder where orderNo=:orderNo")
	public UserAuthOrder getUserAuthOrderByOrderNo(@Param("orderNo")String orderNo);

	@Query("from UserAuthOrder where isRefund=:isRefund and isPaySucc=true and payTime<:payTime")
	public List<UserAuthOrder> getUserAuthOrderListByIsRefund(@Param("isRefund")Boolean isRefund,@Param("payTime")Date payTime);
}
