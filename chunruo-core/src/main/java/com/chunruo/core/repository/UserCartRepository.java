package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserCart;

@Repository("userCartRepository")
public interface UserCartRepository extends GenericRepository<UserCart, Long> {
	
	@Query("from UserCart where userId=:userId order by createTime desc")
	public List<UserCart> getUserCartListByUserId(@Param("userId")Long userId);

	@Query("from UserCart where userId=:userId and productId=:productId")
	public List<UserCart> getUserCartByProductId(@Param("userId")Long userId, @Param("productId")Long productId);
	
	@Query("from UserCart where userId=:userId and productId in(:productIdList)")
	public List<UserCart> getUserCartListByProductIdList(@Param("userId")Long userId, @Param("productIdList")List<Long> productIdList);
}
