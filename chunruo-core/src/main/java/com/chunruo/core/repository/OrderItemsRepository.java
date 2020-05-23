package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.OrderItems;

@Repository("orderItemsRepository")
public interface OrderItemsRepository extends GenericRepository<OrderItems, Long> {

	@Query("from OrderItems where orderId=:orderId order by itemId")
	public List<OrderItems> getOrderItemsListByOrderId(@Param("orderId") Long orderId);
	
	@Query("from OrderItems where orderId=:orderId and groupUniqueBatch=:groupUniqueBatch")
	public List<OrderItems> getOrderItemsListByQroupUniqueBatch(@Param("orderId") Long orderId, @Param("groupUniqueBatch") String groupUniqueBatch);

	@Query("from OrderItems where orderId=:orderId and subOrderId=:subOrderId  order by itemId")
	public List<OrderItems> getOrderSubItemsListByOrderId(@Param("orderId") Long orderId, @Param("subOrderId") Long subOrderId);

	@Query("from OrderItems where orderId in (:orderIdList)  order by itemId")
	public List<OrderItems> getOrderItemsListByOrderIdList(@Param("orderIdList") List<Long> orderIdList);
	
	@Modifying
	@Query("update OrderItems set isEvaluate=:isEvaluate,updateTime=:updateTime where itemId=:itemId")
	public void updateOrderItemsEvaluateStatus(@Param("itemId") Long itemId, @Param("isEvaluate")Boolean isEvaluate, @Param("updateTime")Date updateTime);

	@Query("from OrderItems where subOrderId in (:subOrderIdList)  order by itemId")
	public List<OrderItems> getOrderItemsBySubOrderIdList(@Param("subOrderIdList") List<Long> subOrderIdList);
	
	@Query(value="SELECT * FROM jkd_order_items  " + 
			"WHERE order_id IN ( " + 
				"SELECT order_id FROM jkd_order " + 
				"WHERE status = 4 " + 
				"AND is_invitation_agent = 0 " + 
				"AND store_id =:storeId " + 
				"AND complate_time >:afterTime " + 
			" ) " + 
			"AND is_evaluate = 0 AND is_gift_product = 0 limit 99 ", nativeQuery=true)
	public List<OrderItems> getOrderItemsListByNoEvaluate(@Param("storeId")Long storeId, @Param("afterTime")String afterTime);

	@Query(value="select joi.* from jkd_order jo, jkd_order_items joi " + 
			"where jo.order_id = joi.order_id and jo.user_id =:userId " + 
			"and jo.is_payment_succ = 1 and jo.is_invitation_agent = 0 " + 
			"and jo.status in (2,3,4) and DATE_FORMAT(jo.pay_time, '%Y-%m-%d %H:%i:%s') >=:startPayTime " + 
			"and joi.product_id =:productId ", nativeQuery=true)
	public List<OrderItems> getListByPurchaseLimit(@Param("userId")Long userId, @Param("startPayTime")String startPayTime, @Param("productId")Long productId);
}
