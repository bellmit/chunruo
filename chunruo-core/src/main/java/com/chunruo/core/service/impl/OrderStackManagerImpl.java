package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.OrderStack;
import com.chunruo.core.repository.OrderStackRepository;
import com.chunruo.core.service.OrderStackManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("orderStackManager")
public class OrderStackManagerImpl extends GenericManagerImpl<OrderStack, Long> implements OrderStackManager{
	private OrderStackRepository orderStackRepository;
	
	public OrderStackManagerImpl(OrderStackRepository orderStackRepository) {
		super(orderStackRepository);
		this.orderStackRepository = orderStackRepository;
	}

	@Override
	public List<OrderStack> getOrderStackListByUserId(Long userId) {
		return this.orderStackRepository.getOrderStackListByUserId(userId);
	}

	@Override
	public List<OrderStack> getOrderStackListByGroupKey(String groupKey) {
		return this.orderStackRepository.getOrderStackListByGroupKey(groupKey);
	}

	@Override
	public void deleteAllByOrderStackId(Long orderStackId) {
		OrderStack orderStack = this.get(orderStackId);
		if(orderStack != null && orderStack.getOrderStackId() != null) {
			List<Long> deleteIdList = new ArrayList<Long> ();
			deleteIdList.add(orderStack.getOrderStackId());

			// 检查是否多个商品订单
			if(!StringUtil.isNull(orderStack.getGroupKey())) {
				List<OrderStack> orderStackList = this.getOrderStackListByGroupKey(orderStack.getGroupKey());
				if(orderStackList != null && orderStackList.size() > 0) {
					for(OrderStack stack : orderStackList) {
						deleteIdList.add(stack.getOrderStackId());
					}
				}
			}
			this.deleteByIdList(deleteIdList);
		}
	}
}
