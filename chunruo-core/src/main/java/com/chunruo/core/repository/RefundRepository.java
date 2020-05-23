package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Refund;

@Repository("refundRepository")
public interface RefundRepository extends GenericRepository<Refund, Long> {
	
	@Query("from Refund where orderItemId=:orderItemId and isCurrentTask =:isCurrentTask")
	public List<Refund> getRefundListByOrderItemId(@Param("orderItemId") Long orderItemId, @Param("isCurrentTask") Boolean isCurrentTask);

	@Query("from Refund where userId=:userId and isCurrentTask =:isCurrentTask")
	public List<Refund> getRefundListByUserId(@Param("userId") Long userId, @Param("isCurrentTask") Boolean isCurrentTask);

	@Query("from Refund where updateTime >:updateTime and isCurrentTask = true")
	public List<Refund> getRefundListByUpdateTime(@Param("updateTime") Date updateTime);
	
	@Query("from Refund where isCurrentTask = true and refundStatus=3 and refundType=2 and (expressNumber is null OR expressNumber= '') and updateTime <= :updateTime")
	public List<Refund> getExpressTimeOutRefundList(@Param("updateTime") Date updateTime);
	
	@Query("from Refund where orderId =:orderId and isCurrentTask =:isCurrentTask")
	public List<Refund> getRefundListByOrderId(@Param("orderId")Long orderId, @Param("isCurrentTask")Boolean isCurrentTask);
	
	@Query("from Refund where refundStatus =:refundStatus and refundId in (:refundIdList)")
	public List<Refund> getRefundListByIdList(@Param("refundIdList")List<Long> refundIdList, @Param("refundStatus")Integer refundStatus);

	@Query("from Refund where refundType in(1,2) and isCurrentTask = :isCurrentTask and  (updateTime BETWEEN :beginDate and :endDate)")
	public List<Refund> getRefundListByTime(@Param("beginDate")Date beginDate, @Param("endDate")Date endDate,@Param("isCurrentTask")Boolean isCurrentTask);

	@Query("from Refund where storeId=:storeId and isCurrentTask =:isCurrentTask")
	public List<Refund> getRefundListByStoreId(@Param("storeId")Long storeId,@Param("isCurrentTask") Boolean isCurrentTask);

	@Query("from Refund where orderId in(:orderIdList) and isCurrentTask = true")
	public List<Refund> getRefundListByOrderIdList(@Param("orderIdList")List<Long> orderIdList);

}
