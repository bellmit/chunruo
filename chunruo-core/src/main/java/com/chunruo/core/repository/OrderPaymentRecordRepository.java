package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderPaymentRecord;

@Repository("orderPaymentRecordRepository")
public interface OrderPaymentRecordRepository extends GenericRepository<OrderPaymentRecord, Long> {
	
	@Query("from OrderPaymentRecord where orderId=:orderId")
	public List<OrderPaymentRecord> getListByOrderId(@Param("orderId") Long orderId);
	
	@Query("from OrderPaymentRecord where batchNumber =:batchNumber")
	public List<OrderPaymentRecord> getOrderPaymentRecordListByBatchNumber(@Param("batchNumber")String batchNumber);

	@Query("from OrderPaymentRecord where orderId=:orderId and orderNo=:orderNo and paymentType=:paymentType and weChatConfigId=:weChatConfigId")
	public List<OrderPaymentRecord> getListByOrderIdAndPaymentType(@Param("orderId")Long orderId,@Param("orderNo")String orderNo, @Param("paymentType")Integer paymentType,@Param("weChatConfigId")Long weChatConfigId);
	
	@Query("from OrderPaymentRecord where orderId=:orderId and orderNo=:orderNo and paymentType=:paymentType")
	public List<OrderPaymentRecord> getListByOrderIdAndPaymentType(@Param("orderId")Long orderId, @Param("orderNo")String orderNo, @Param("paymentType")Integer paymentType);
	
}
