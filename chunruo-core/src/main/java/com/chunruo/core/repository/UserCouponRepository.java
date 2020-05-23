package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserCoupon;

@Repository("userCouponRepository")
public interface UserCouponRepository extends GenericRepository<UserCoupon, Long> {

	@Query("from UserCoupon where userId =:userId order by createTime desc")
	public List<UserCoupon> getUserCouponListByUserId(@Param("userId") Long userId);
	
	@Query("from UserCoupon where userId =:userId and couponTaskId =:couponTaskId order by createTime desc")
	public List<UserCoupon> getUserCouponListByUserIdTaskId(@Param("userId") Long userId, @Param("couponTaskId") Long couponTaskId);

	@Query("from UserCoupon where couponStatus =:couponStatus")
	public List<UserCoupon> getUserCouponListByStatus(@Param("couponStatus") Integer couponStatus);

	@Query("from UserCoupon where updateTime >:updateTime")
	public List<UserCoupon> getUserCouponListByUpdateTime(@Param("updateTime") Date updateTime);
	
	@Query("from UserCoupon where couponId =:couponId")
	public List<UserCoupon> getUserCouponListByCouponId(@Param("couponId") Long couponId);
	
	@Query("from UserCoupon where (createTime BETWEEN :beginDate and :endDate) and userId =:userId")
	public List<UserCoupon> getUserCouponListByCreateTime(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("userId") Long userId);

	@Modifying
	@Query("update UserCoupon set updateTime =now(),couponStatus=:couponStatus where userCouponId=:userCouponId")
	public void updateUserCouponStatus(@Param("couponStatus") Integer couponStatus, @Param("userCouponId")Long userCouponId);

	@Modifying
	@Query("update UserCoupon set updateTime =now(),couponStatus=:couponStatus where userCouponId in(:userCouponIdList)")
	public void updateUserCouponStatus(@Param("couponStatus")Integer couponStatus, @Param("userCouponIdList")List<Long> userCouponIdList);
}
