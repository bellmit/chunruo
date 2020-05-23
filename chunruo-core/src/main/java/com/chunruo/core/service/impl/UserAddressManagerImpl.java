package com.chunruo.core.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserAddress;
import com.chunruo.core.repository.UserAddressRepository;
import com.chunruo.core.service.UserAddressManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userAddressManager")
public class UserAddressManagerImpl extends GenericManagerImpl<UserAddress, Long> implements UserAddressManager{
	private UserAddressRepository userAddressRepository;
	
	@Autowired
	public UserAddressManagerImpl(UserAddressRepository userAddressRepository) {
		super(userAddressRepository);
		this.userAddressRepository = userAddressRepository;
	}

	@Override
	public List<UserAddress> getUserAddressListByUserId(Long userId) {
		return this.userAddressRepository.getUserAddressListByUserId(userId);
	}
	
	@Override
	public UserAddress getDefaultUserAddressListByUserId(Long userId){
		List<UserAddress> list = this.userAddressRepository.getDefaultUserAddressListByUserId(userId);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}
	
	@Override
	public void updateUserAddressDefault(boolean isDefault, Long userId){
		this.userAddressRepository.updateUserAddressDefault(isDefault, userId);
	}
	
	@Override
	public UserAddress getUserAddressByAddressId(Long addressId, Long userId){
		return this.userAddressRepository.getUserAddressByAddressId(addressId, userId);
	}
	
	@Override
	public UserAddress saveUserAddress(UserAddress userAddress, Boolean isDefaultBak){
		userAddress.setUpdateTime(DateUtil.getCurrentDate());
		userAddress = this.save(userAddress);
		
		Long userId = userAddress.getUserId();
		Long addressId = userAddress.getAddressId();
		Boolean isDefault = userAddress.getIsDefault();
		if(StringUtil.nullToBoolean(isDefault)){
			this.userAddressRepository.updateUserAddressDefault(false, userId, addressId);
		}else if(isDefaultBak){
			List<UserAddress> addressList = this.userAddressRepository.getUserAddress(userId, addressId);
			if(!CollectionUtils.isEmpty(addressList)){
				UserAddress userAddressBak = addressList.get(0);
				userAddressBak.setIsDefault(true);
				this.save(userAddressBak);
			}
		}
		return userAddress;
	}
	
	@Override
	public UserAddress setDefaultAddress(UserAddress userAddress){
		userAddress.setIsDefault(true);
		userAddress.setUpdateTime(DateUtil.getCurrentDate());
		userAddress = this.save(userAddress);
		this.userAddressRepository.updateUserAddressDefault(false, userAddress.getUserId(), userAddress.getAddressId());
		return userAddress;
	}

	@Override
	public void removeUserAddressById(UserAddress userAddress) {
		Long addressId = userAddress.getAddressId();
		Long userId = userAddress.getUserId();
		this.remove(addressId);
		
		// 删除的是默认地址,需要补其他未默认地址
		if(StringUtil.nullToBoolean(userAddress.getIsDefault())){
			List<UserAddress> addressList = userAddressRepository.getUserAddress(userId, addressId);
			if(!CollectionUtils.isEmpty(addressList)){
				UserAddress userAddressBak = addressList.get(0);
				userAddressBak.setIsDefault(true);
				this.save(userAddressBak);
			}
		}
	}
}
