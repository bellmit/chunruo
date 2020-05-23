package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserAddress;

@Repository("userAddressRepository")
public interface UserAddressRepository extends GenericRepository<UserAddress, Long> {

	@Query("from UserAddress where userId=:userId order by createTime desc")
	public List<UserAddress> getUserAddressListByUserId(@Param("userId")Long userId);
	
	@Query("from UserAddress where userId=:userId and isDefault = true")
	public List<UserAddress> getDefaultUserAddressListByUserId(@Param("userId")Long userId);
	
	@Query("from UserAddress where addressId=:addressId and userId=:userId")
	public UserAddress getUserAddressByAddressId(@Param("addressId")Long addressId, @Param("userId")Long userId);
	
	@Query("from UserAddress where userId=:userId and addressId <> :addressId order by createTime desc")
	public List<UserAddress> getUserAddress(@Param("userId")Long userId, @Param("addressId")Long addressId);
	
	@Modifying
	@Query("update UserAddress p set p.isDefault =:isDefault where p.userId =:userId")
    void updateUserAddressDefault(@Param("isDefault")boolean isDefault ,@Param("userId") Long userId);
	
	@Modifying
	@Query("update UserAddress p set p.isDefault =:isDefault where p.userId =:userId and p.addressId <> :addressId ")
    void updateUserAddressDefault(@Param("isDefault")boolean isDefault ,@Param("userId") Long userId, @Param("addressId") Long addressId);
}
