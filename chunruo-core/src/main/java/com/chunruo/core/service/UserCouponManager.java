package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.vo.MsgModel;

public interface UserCouponManager  extends GenericManager<UserCoupon, Long>{

	public List<UserCoupon> getUserCouponListByUserId(Long userId);

	public List<UserCoupon> getUserCouponListByUserIdTaskId(Long userId, Long taskId);

	public MsgModel<UserCoupon> saveUserCoupon(CouponTask couponTask, Long userId, Boolean isRepeat);

	public void fristLoginGiveCoupon(UserInfo userInfo);

	public void fristBuyGiftPackageGiveCoupon(UserInfo userInfo);

	public void inviteDealerGiveCoupon(UserInfo userInfo);

	public boolean addStoreSalesGiveCoupon(UserInfo userInfo, CouponTask task);

	public void totalShareGiveCoupon(UserInfo userInfo, Integer shareCount);

	public List<UserCoupon> getUserCouponListByStatus(int status);

	public List<UserCoupon> getUserCouponListByUpdateTime(Date updateTime);

	public List<UserCoupon> getUserCouponListByCouponId(Long couponId);
	
	public boolean batchInsertUserCoupon(List<UserCoupon> modelList, int commitPerCount); 

	public List<UserCoupon> getUserCouponListByCreateTime(Date beginDate, Date endDate, Long userId);

	public void firstUploadCardGiveCoupon(UserInfo userInfo);
	
	public void inviteVipGiveCoupon(UserInfo userInfo);
	
	public boolean intoWeiXinFlockGiveCoupon(UserInfo userInfo);
	
	public boolean voteActivityGiveCoupon(UserInfo userInfo, CouponTask task);

	public void partTakeVoteGiveCoupon(UserInfo userInfo);
	
	public MsgModel<List<UserCoupon>> saveUserCouponList(CouponTask couponTask, Long userId, int couponNumber);
	
	public void rechargeGiveProductCoupon(Long userId,Long productId,Long rechargeTemplateId);

	public void sendMemberCoupon(List<Long> couponIdList,Long userId);

	public void updateUserCouponStatus(int userCouponStatucDelete,List<Long> userCouponIdList);
	
}
