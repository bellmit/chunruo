package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserAddress;

public interface UserAddressManager extends GenericManager<UserAddress, Long>{

	public List<UserAddress> getUserAddressListByUserId(Long userId);
	
	public UserAddress getDefaultUserAddressListByUserId(Long userId);
	
	public UserAddress getUserAddressByAddressId(Long addressId, Long userId);
	
	public UserAddress setDefaultAddress(UserAddress userAddress);

	public void updateUserAddressDefault(boolean isDefault, Long userId);
	
	public UserAddress saveUserAddress(UserAddress userAddress, Boolean isDefault);
	
	public void removeUserAddressById(UserAddress userAddress);
}
