package com.chunruo.core.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.repository.UserCartRepository;
import com.chunruo.core.service.UserCartManager;

@Transactional
@Component("userCartManager")
public class UserCartManagerImpl extends GenericManagerImpl<UserCart, Long> implements UserCartManager{
	private UserCartRepository userCartRepository;
	
	@Autowired
	public UserCartManagerImpl(UserCartRepository userCartRepository) {
		super(userCartRepository);
		this.userCartRepository = userCartRepository;
	}

	@Override
	public List<UserCart> getUserCartListByUserId(Long userId) {
		return this.userCartRepository.getUserCartListByUserId(userId);
	}

	@Override
	public List<UserCart> getUserCartByProductId(Long userId, Long productId) {
		return this.userCartRepository.getUserCartByProductId(userId, productId);
	}

	@Override
	public List<UserCart> getUserCartListByProductIdList(Long userId, List<Long> prodcutIdList) {
		if(prodcutIdList == null || prodcutIdList.size() <= 0){
			return null;
		}
		return this.userCartRepository.getUserCartListByProductIdList(userId, prodcutIdList);
	}
}
