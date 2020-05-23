package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserCart;

public interface UserCartManager extends GenericManager<UserCart, Long>{

	public List<UserCart> getUserCartListByUserId(Long userId);
	
	public List<UserCart> getUserCartByProductId(Long userId, Long productId);
	
	public List<UserCart> getUserCartListByProductIdList(Long userId, List<Long> prodcutIdList);
}
