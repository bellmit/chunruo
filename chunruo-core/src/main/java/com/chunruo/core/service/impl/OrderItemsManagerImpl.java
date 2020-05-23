package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.repository.OrderItemsRepository;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderItemsManager")
public class OrderItemsManagerImpl extends GenericManagerImpl<OrderItems, Long> implements OrderItemsManager {
	private OrderItemsRepository orderItemsRepository;

	@Autowired
	public OrderItemsManagerImpl(OrderItemsRepository orderItemsRepository) {
		super(orderItemsRepository);
		this.orderItemsRepository = orderItemsRepository;
	}

	@Override
	public List<OrderItems> getOrderItemsByOrderIdList(List<Long> orderIdList) {
		if(orderIdList == null || orderIdList.size() <= 0){
			return null;
		}
		return this.orderItemsRepository.getOrderItemsListByOrderIdList(orderIdList);
	}
	
	@Override
	public List<OrderItems> getOrderItemsListByOrderId(Long orderId) {
		return this.orderItemsRepository.getOrderItemsListByOrderId(orderId);
	}

	@Override
	public List<OrderItems> getOrderSubItemsListByOrderId(Long orderId, Long subOrderId) {
		return this.orderItemsRepository.getOrderSubItemsListByOrderId(orderId, subOrderId);
	}

	@Override
	public List<OrderItems> getOrderItemsListByQroupUniqueBatch(Long orderId, String groupUniqueBatch) {
		return this.orderItemsRepository.getOrderItemsListByQroupUniqueBatch(orderId, groupUniqueBatch);
	}

	@Override
	public void updateOrderItemsEvaluateStatus(Long itemId, Boolean isEvaluate) {
		this.orderItemsRepository.updateOrderItemsEvaluateStatus(itemId, isEvaluate, DateUtil.getCurrentDate());
	}

	@Override
	public List<Object[]> getOrderItemListByIsInvitationAgentOrder(Boolean isInvitationAgent) {
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select product_id ,max(number) from ( select joi.product_id,count(*) as number from jkd_order_items joi where joi.order_id in( ");
		strBulSql.append("select jo.order_id from jkd_order jo where jo.is_payment_succ = true and status in(2,3,4) and member_template_id is not null and jo.is_invitation_agent = "+isInvitationAgent);
		strBulSql.append(") group by date_format(joi.create_time,'%y-%m-%d') ) aa group by product_id");
		return this.querySql(strBulSql.toString());
	}

	@Override
	public List<Object[]> getOrderItemsByOrderNoList(List<String> orderNoList) {
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("SELECT jo.order_no,joi.product_name,joi.product_tags FROM jkd_order_items joi,jkd_order jo WHERE joi.order_id = jo.order_id AND jo.order_no IN("+StringUtil.stringArrayToString(orderNoList)+") GROUP BY joi.order_id;");
		return this.querySql(strBulSql.toString());
	}

	@Override
	public List<OrderItems> getOrderItemsBySubOrderIdList(List<Long> subOrderIdList) {
		if(subOrderIdList != null && subOrderIdList.size() > 0) {
			return this.orderItemsRepository.getOrderItemsBySubOrderIdList(subOrderIdList);
		}
		return null;
	}

	@Override
	public List<OrderItems> getOrderItemsListByNoEvaluate(Long storeId) {
		Date effectiveDate = DateUtil.getDateBeforeByDay(DateUtil.getCurrentDate(), 30);
		String afterTime = DateUtil.formatDate(DateUtil.DATE_FORMAT, effectiveDate);
		return this.orderItemsRepository.getOrderItemsListByNoEvaluate(storeId, afterTime);
	}
	
	@Override
	public List<OrderItems> getListByPurchaseLimit(Long userId, String startPayTime, Long productId) {
		return this.orderItemsRepository.getListByPurchaseLimit(userId, startPayTime, productId);
	}
}
