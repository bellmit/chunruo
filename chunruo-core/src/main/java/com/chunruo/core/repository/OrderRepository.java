package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Order;

@Repository("orderRepository")
public interface OrderRepository extends GenericRepository<Order, Long> {

	@Query("from Order where status=:status and isSubOrder=:isSubOrder and isDelete = false order by createTime desc")
	public List<Order> getOrderListByStatus(@Param("status") Integer status, @Param("isSubOrder") boolean isSubOrder);

	@Query("from Order where status=:status")
	public List<Order> getOrderListByStatus(@Param("status") Integer status);

	@Query("from Order where orderNo=:orderNo")
	public Order getOrderByOrderNo(@Param("orderNo") String orderNo);

	@Query("from Order where isSplitSingle=false and isSubOrder=true and parentOrderId=:parentOrderId order by createTime desc")
	public List<Order> getOrderSubListByParentOrderId(@Param("parentOrderId") Long parentOrderId);
	
	@Transactional
	@Modifying
	@Query("update Order set isCheck =:isCheck, updateTime=now() where orderId =:orderId")
	public void updateOrderCheckById(@Param("isCheck") boolean isCheck, @Param("orderId") Long orderId);

	@Query(value="select * from jkd_order where user_id=:userId " + 
			"and is_sub_order=0 " + 
			"and is_delete=0 " + 
			"order by create_time desc limit :limit", nativeQuery=true)
	public List<Order> getOrderListByUserId(@Param("userId") Long userId, @Param("limit")int limit);

	@Query("from Order where userId in (:userIdList)")
	public List<Order> getOrderListByUserIdList(@Param("userIdList") List<Long> userIdList);

	@Query("from Order where orderId in (:orderIdList)")
	public List<Order> getOrderListByOrderIds(@Param("orderIdList") List<Long> orderIdList);

	@Query("from Order where orderNo in (:orderNoList)")
	public List<Order> getOrderListByOrderNoList(@Param("orderNoList") List<String> orderNoList);

	@Transactional
	@Modifying
	@Query("update Order set isDelete = :isDelete, updateTime=now() where orderId =:orderId")
	public void deleteOrder(@Param("isDelete") Boolean isDelete, @Param("orderId") Long orderId);

	@Transactional
	@Modifying
	@Query("update Order set status =:status, updateTime=now(), sentTime=now() where orderId =:orderId")
	public void updateParentOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

	@Transactional
	@Modifying
	@Query("update Order set status =:status, updateTime=now(), complateTime =now() where orderId =:orderId")
	public void updateOrderCompleteStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

	@Transactional
	@Modifying
	@Query("update Order set status =:status, updateTime=now(), complateTime=now() where orderId in (:orderIdList)")
	public void updateOrderCompleteStatus(@Param("orderIdList") List<Long> orderIdList, @Param("status") Integer status);

	@Transactional
	@Modifying
	@Query("update Order set status =:status, updateTime=now(), isIntercept=false where orderId in (:orderIdList)")
	public void updateOrderStatus(@Param("orderIdList") List<Long> orderIdList, @Param("status") Integer status);

	@Transactional
	@Modifying
	@Query("update Order set tradeNo =:tradeNo,paymentType=:paymentType,weChatConfigId=:weChatConfigId where orderId =:orderId and status =:status and isDelete = false and isPaymentSucc = false and tradeNo is null")
	public void updateOrderPaymentTradeNo(@Param("orderId") Long orderId, @Param("tradeNo") String tradeNo,
			@Param("status") Integer status, @Param("paymentType") Integer paymentType,
			@Param("weChatConfigId") Long weChatConfigId);

	@Query("from Order where batchNumber =:batchNumber")
	public List<Order> getOrderListByBatchNumber(@Param("batchNumber") String batchNumber);

	@Query("from Order where updateTime >:updateTime")
	public List<Order> getOrderListByUpdateTime(@Param("updateTime") Date updateTime);

	@Query("from Order where status=:status and isSubOrder=:isSubOrder and createTime > :beginDate and createTime < :endDate order by createTime desc")
	public List<Order> getOrderListAfterCreateTime(@Param("status") Integer status,
			@Param("isSubOrder") boolean isSubOrder, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate);

	@Query("from Order where status=:status and isDirectPushErp=true and isSplitSingle=false and syncTime <:beforeDate")
	public List<Order> getOrderStatusListBeforeSyncTime(@Param("status") Integer status,
			@Param("beforeDate") Date beforeDate);

	@Query("from Order where status=:status and complateTime < :complateTime and isSubOrder=:isSubOrder and isCheck = :isCheck order by createTime desc")
	public List<Order> getCheckOrders(@Param("status") Integer status, @Param("complateTime") Date complateTime,
			@Param("isSubOrder") boolean isSubOrder, @Param("isCheck") boolean isCheck);

	@Query("from Order where (createTime BETWEEN :beginDate and :endDate) and userId in (:userIdList) order by createTime desc")
	public List<Order> getOrderListByTime(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
			@Param("userIdList") List<Long> userIdList);

	@Query("from Order where userId =:userId and status=:status and (createTime BETWEEN :beginDate and :endDate) and isSubOrder=false")
	public List<Order> getOrderListByCreateTime(@Param("status") Integer status, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
			@Param("userId") Long userId);

	@Query("from Order where (createTime BETWEEN :beginDate and :endDate) order by createTime desc")
	public List<Order> getOrderListByTime(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

	@Query("from Order WHERE hour(timediff(now(),payTime)) > 72 AND status = 2")
	public List<Order> getAbmormalOrderList();

	@Query("from Order where userCouponId in (:userCouponIdList) order by createTime desc")
	public List<Order> getCouponOrderListByUserCouponId(@Param("userCouponIdList") List<Long> userCouponIdList);
	
	@Query("from Order where status in (2,3,4) and createTime > :createTime  and userId=:userId")
	public List<Order> getBecameVipOrderList(@Param("userId") Long userId,@Param("createTime") Date createTime);

	@Query("from Order where orderNo like  CONCAT('%', :orderNo ,'%')")
	public List<Order> getOrderListByLikeParentOrderNo(@Param("orderNo") String orderNo);
	
	@Query("from Order where storeId=:storeId and isSeckillLimit=:isSeckillLimit")
	public List<Order> getOrderListByisSeckillLimit(@Param("storeId") Long storeId,@Param("isSeckillLimit") Boolean isSeckillLimit);

	@Query("from Order where shareUserId=:shareUserId and isShareBuy = true")
	public List<Order> getOrderListByShareUserId(@Param("shareUserId")Long shareUserId);

	@Query(value="select * from jkd_order where store_id=:storeId " + 
			"and is_sub_order=0 " + 
			"and is_delete=0 " + 
			"order by create_time desc limit :limit", nativeQuery=true)
	public List<Order> getOrderListByStoreId(@Param("storeId")Long storeId, @Param("limit")int limit);

	@Transactional
	@Modifying
	@Query("update Order set status =:status, updateTime=now() where parentOrderId =:parentOrderId")
	public void updateSubOrderStatusByParentOrderId(@Param("parentOrderId")Long parentOrderId, @Param("status")Integer status);
}
