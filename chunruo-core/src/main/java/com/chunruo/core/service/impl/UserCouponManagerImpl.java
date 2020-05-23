package com.chunruo.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserCouponStatus;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.CouponTask;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.repository.UserCouponRepository;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.UserCouponManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userCouponManager")
public class UserCouponManagerImpl extends GenericManagerImpl<UserCoupon, Long> implements UserCouponManager {
	private UserCouponRepository userCouponRepository;
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private ProductManager productManager;
	
	@Autowired
	public UserCouponManagerImpl(UserCouponRepository userCouponRepository) {
		super(userCouponRepository);
		this.userCouponRepository = userCouponRepository;
	}
	
	@Override
	public List<UserCoupon> getUserCouponListByUpdateTime(Date updateTime){
		return userCouponRepository.getUserCouponListByUpdateTime(updateTime);
	}
	
	@Override
	public List<UserCoupon> getUserCouponListByCouponId(Long couponId){
		return userCouponRepository.getUserCouponListByCouponId(couponId);
	}

	@Override
	public List<UserCoupon> getUserCouponListByUserId(Long userId) {
		try {
			List<UserCoupon> list = this.userCouponRepository.getUserCouponListByUserId(userId);
			List<UserCoupon> resultList = new ArrayList<>();
			if(list != null && list.size() > 0){
				this.detach(list);
				List<Integer> tiemOutStatusUserCouponList = new ArrayList<Integer> ();
				tiemOutStatusUserCouponList.add(UserCoupon.USER_COUPON_STATUS_NOT_USED);
				tiemOutStatusUserCouponList.add(UserCoupon.USER_COUPON_STATUS_OCCUPIED);
				
				Date currentDate = DateUtil.getFormatDate(DateUtil.getCurrentDate(),DateUtil.DATE_FORMAT_YEAR);
				Long currentTimes = currentDate.getTime();
				for(UserCoupon userCoupon : list){ 
					if(StringUtil.compareObject(StringUtil.nullToInteger(userCoupon.getCouponStatus()), UserCoupon.USER_COUPON_STATUC_DELETE)) {
						continue;
					}
					//检查优惠券
					Coupon coupon = Constants.COUPON_MAP.get(userCoupon.getCouponId());
					if(coupon == null || coupon.getCouponId() == null) {
						//如果优惠券不存在，直接查询数据库
						coupon = this.couponManager.get(userCoupon.getCouponId());
//						MsgModel<Void> msgModel = CoreInitUtil.setCouponInfo(coupon);
//						if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
//							Constants.COUPON_MAP.put(coupon.getCouponId(), coupon);
//						}
					}
					
					if(coupon == null || coupon.getCouponId() == null){
						continue;
					}
					
					Long receiveTime = DateUtil.getMillSeconds(DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, StringUtil.null2Str(userCoupon.getReceiveTime())));
					Long effectiveTime = DateUtil.getMillSeconds(DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, StringUtil.null2Str(userCoupon.getEffectiveTime())));
					
					Long receiveBeginTime = DateUtil.getMillSeconds(DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, coupon.getReceiveBeginTime()));
					Long receiveEndTime = DateUtil.getMillSeconds(DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, coupon.getReceiveEndTime()));

					if(receiveTime.compareTo(receiveBeginTime) < 0) {
						//用户券到账时间小于优惠券限制开始时间
						userCoupon.setReceiveTime(DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, coupon.getReceiveBeginTime())));
					}
					if(receiveEndTime.compareTo(effectiveTime) < 0) {
						//用户券到期时间大于优惠券限制结束时间
						
						userCoupon.setEffectiveTime(DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.parseDate(DateUtil.DATE_FORMAT_YEAR, coupon.getReceiveEndTime())));
						effectiveTime = receiveEndTime;
					}
					if(tiemOutStatusUserCouponList.contains(StringUtil.nullToInteger(userCoupon.getCouponStatus()))) {
						try {
							if(effectiveTime.longValue() < currentTimes.longValue()) {
								userCoupon.setUpdateTime(DateUtil.getCurrentDate());
								userCoupon.setCouponStatus(UserCoupon.USER_COUPON_STATUS_TIME_OUT);
								this.userCouponRepository.updateUserCouponStatus(UserCoupon.USER_COUPON_STATUS_TIME_OUT, userCoupon.getUserCouponId());
							}
						}catch(Exception e) {
							e.printStackTrace();
							continue;
						}
					}
					
					userCoupon.setCoupon(coupon);
					resultList.add(userCoupon);
				}
			}
			return resultList;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<UserCoupon> getUserCouponListByStatus(int status) {
		List<UserCoupon> list = this.userCouponRepository.getUserCouponListByStatus(status);
		List<UserCoupon> resultList = new ArrayList<>();
		if(list != null && list.size() > 0){
			for(UserCoupon userCoupon : list){
				Coupon coupon = Constants.COUPON_MAP.get(userCoupon.getCouponId());
				if(coupon != null && coupon.getCouponId() != null){
					userCoupon.setCoupon(coupon);
					resultList.add(userCoupon);
				}
			}
		}
		return resultList;
	}
	
	@Override
	public List<UserCoupon> getUserCouponListByUserIdTaskId(Long userId, Long taskId) {
		return this.userCouponRepository.getUserCouponListByUserIdTaskId(userId, taskId);
	}
	
	/**
	 * @param couponTask      优惠券类型
	 * @param userId          用户ID
	 * @param isRepeat        是否可以重复领取
	 * @return   UserCoupon
	 */
	@Override
	public MsgModel<UserCoupon> saveUserCoupon(CouponTask couponTask, Long userId, Boolean isRepeat){
		MsgModel<UserCoupon> msgModel = new MsgModel<UserCoupon> ();
		
		//判断是否可以重复领取,如果不可以则查询领券记录
		if(!isRepeat){
			List<UserCoupon> userCouponList = this.getUserCouponListByUserIdTaskId(userId, couponTask.getTaskId());
			if(userCouponList != null && userCouponList.size() > 0){
				msgModel.setIsSucc(false);
				return msgModel;
			}
		}
		
		// 优惠券发放
		Coupon coupon = couponManager.get(couponTask.getCouponId());
		if(coupon != null && coupon.getCouponId() != null){
			String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
			String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, new Date());
			UserCoupon userCoupon = new UserCoupon();
			userCoupon.setCouponNo(CoreInitUtil.getRandomNo());
			userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
			userCoupon.setCouponTaskId(couponTask.getTaskId());
			userCoupon.setReceiveTime(receiveTime);
			userCoupon.setEffectiveTime(effectiveTime);
			userCoupon.setUserId(userId);
			userCoupon.setCouponId(coupon.getCouponId());
			userCoupon.setIsGiftCoupon(StringUtil.nullToBoolean(coupon.getIsGiftCoupon()));
			userCoupon.setIsShowGet(true);
			userCoupon.setCreateTime(DateUtil.getCurrentDate());
			userCoupon.setUpdateTime(userCoupon.getCreateTime());
			userCoupon = this.userCouponRepository.save(userCoupon);
			
			msgModel.setIsSucc(true);
			msgModel.setData(userCoupon);
			return msgModel;
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	
	/**
	 * @param couponTask      优惠券类型
	 * @param userId          用户ID
	 * @param isRepeat        是否可以重复领取
	 * @return   UserCoupon
	 */
	@Override
	public MsgModel<List<UserCoupon>> saveUserCouponList(CouponTask couponTask, Long userId, int couponNumber){
		MsgModel<List<UserCoupon>> msgModel = new MsgModel<List<UserCoupon>> ();
		
		List<UserCoupon> userCouponList = new ArrayList<UserCoupon>();
		// 优惠券发放
		Coupon coupon = couponManager.get(couponTask.getCouponId());
		if(coupon != null && coupon.getCouponId() != null){
			String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
			String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, new Date());
			for(int i=0;i<couponNumber;i++) {
				UserCoupon userCoupon = new UserCoupon();
				userCoupon.setCouponNo(CoreInitUtil.getRandomNo());
				userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
				userCoupon.setCouponTaskId(couponTask.getTaskId());
				userCoupon.setReceiveTime(receiveTime);
				userCoupon.setEffectiveTime(effectiveTime);
				userCoupon.setUserId(userId);
				userCoupon.setCouponId(coupon.getCouponId());
				userCoupon.setIsGiftCoupon(StringUtil.nullToBoolean(coupon.getIsGiftCoupon()));
				userCoupon.setIsShowGet(true);
				userCoupon.setCreateTime(DateUtil.getCurrentDate());
				userCoupon.setUpdateTime(userCoupon.getCreateTime());
				userCouponList.add(userCoupon);
			}
			
			//批量保存优惠券
			this.batchInsert(userCouponList, userCouponList.size());
			
			msgModel.setIsSucc(true);
			msgModel.setData(userCouponList);
			return msgModel;
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 店长注册后首次登录APP 送代金券
	 */
	@Override
	public void fristLoginGiveCoupon(UserInfo userInfo) {
		List<Integer> userLevelList = new ArrayList<Integer>();
		userLevelList.add(UserLevel.USER_LEVEL_BUYERS);
		userLevelList.add(UserLevel.USER_LEVEL_DEALER);
		userLevelList.add(UserLevel.USER_LEVEL_AGENT);
		userLevelList.add(UserLevel.USER_LEVEL_V2);
		userLevelList.add(UserLevel.USER_LEVEL_V3);
		if (userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))
				&& StringUtil.compareObject(StringUtil.nullToInteger(userInfo.getLoginCount()), 1)
				&& userInfo.getIsAgent()) {
			CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_FIRST_LOGIN);
			if (task != null && task.getTaskId() != null) {
				this.saveUserCoupon(task, userInfo.getUserId(), false);
			}
		}
	}

	/**
	 * 首次购买礼包送代金券
	 */
	@Override
	public void fristBuyGiftPackageGiveCoupon(UserInfo userInfo) {
		if (StringUtil.compareObject(StringUtil.nullToInteger(userInfo.getLevel()), UserLevel.USER_LEVEL_BUYERS)) {
			CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_FIRST_BUY_PACKGE);
			if (task != null && task.getTaskId() != null) {
				this.saveUserCoupon(task, userInfo.getUserId(), false);
			}
		}
	}

	/**
	 * 邀请代理商送满减券
	 */
	@Override
	public void inviteDealerGiveCoupon(UserInfo userInfo) {
		if (StringUtil.compareObject(StringUtil.nullToInteger(userInfo.getLevel()), UserLevel.USER_LEVEL_DEALER)) {
			CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_INVITE_TEAM);
			if (task != null && task.getTaskId() != null) {
				this.saveUserCoupon(task, userInfo.getUserId(), true);
			}
		}
	}
	
	/**
	 * 第一次上传二维码送满减券
	 */
	@Override
	public void firstUploadCardGiveCoupon(UserInfo userInfo) {
		CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_UPLOAD_CARD);
		if (task != null && task.getTaskId() != null) {
			this.saveUserCoupon(task, userInfo.getUserId(), false);
		}
	}

	/**
	 * 累计销售达到指定数额送代金券
	 */
	@Override
	public boolean addStoreSalesGiveCoupon(UserInfo userInfo, CouponTask task) {
		boolean isAddStoreSalesGiveCoupon = false;
		if(task != null 
				&& userInfo != null
				&& userInfo.getUserId() != null
				&& task.getTaskId() != null
				&& StringUtil.isDouble(task.getTaskContent())
				&& StringUtil.nullToDoubleFormat(task.getTaskContent()) <= StringUtil.nullToDoubleFormat(userInfo.getSales())){
			MsgModel<UserCoupon> msgModel = this.saveUserCoupon(task, userInfo.getUserId(), false);
			if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
				// 发放优惠券成功
				isAddStoreSalesGiveCoupon = true;
			}
		}
		return isAddStoreSalesGiveCoupon;
	}

	/**
	 * 累计分享商品次数达到送代金券
	 */
	@Override
	public void totalShareGiveCoupon(UserInfo userInfo,Integer shareCount) {
		CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_TOTAL_SHARE);
		if (task != null && task.getTaskId() != null) {
			Coupon coupon = couponManager.get(task.getCouponId());
			if (coupon != null 
					&& coupon.getCouponId() != null 
					&& StringUtil.nullToInteger(shareCount) >= StringUtil.nullToInteger(task.getTaskContent())) {
				this.saveUserCoupon(task, userInfo.getUserId(), false);
			}
		}
	}

	@Override
	public boolean batchInsertUserCoupon(List<UserCoupon> modelList, int commitPerCount) {
		Long begin = new Date().getTime();  // 开时时间  
		boolean result = true;
		Connection conn = null;
		int commitCount = (commitPerCount / 5000); //5000条默认提交一次,需提交次数

		try { 
			StringBuffer sqlPrefixBuffer = new StringBuffer();
			sqlPrefixBuffer.append("insert into jkd_user_coupon(");
			sqlPrefixBuffer.append("coupon_id,");
			sqlPrefixBuffer.append("coupon_no,"); 
			sqlPrefixBuffer.append("coupon_status,"); 
			sqlPrefixBuffer.append("coupon_task_id,"); 
			sqlPrefixBuffer.append("user_id,");
			sqlPrefixBuffer.append("is_gift_coupon,");
			sqlPrefixBuffer.append("effective_time,"); 
			sqlPrefixBuffer.append("receive_time,"); 
			sqlPrefixBuffer.append("create_time,"); 
			sqlPrefixBuffer.append("update_time"); 
			sqlPrefixBuffer.append(") values ");

			conn = this.getJdbcTemplate().getDataSource().getConnection();
			StringBuffer suffix = new StringBuffer();  
			conn.setAutoCommit(false);   // 设置事务为非自动提交  
			// 比起st，pst会更好些  
			PreparedStatement pst = conn.prepareStatement("");  
			// 外层循环，总提交事务次数  
			for (int i = 0; i <= commitCount; i++) {  
				// 第N次提交步长  
				List<UserCoupon> subUserList = modelList.subList(5000 * i, 5000 * (i + 1) > commitPerCount ? commitPerCount : 5000 * (i + 1));
				for (int j = 0; j < subUserList.size(); j++) { 
					Date currentDate = DateUtil.getCurrentDate();
					UserCoupon model = subUserList.get(j);
					// 构建sql
					suffix.append("(");
					suffix.append(model.getCouponId() + ",");
					suffix.append(String.format("'%s',", model.getCouponNo()));
					suffix.append(model.getCouponStatus()+ ",");
					suffix.append(model.getCouponTaskId()+ ",");
					suffix.append(model.getUserId() + ",");
					suffix.append(StringUtil.nullToBoolean(model.getIsGiftCoupon()) + ",");
					suffix.append(String.format("'%s',", model.getEffectiveTime()));
					suffix.append(String.format("'%s',", model.getReceiveTime()));
					suffix.append(String.format("'%s',", DateUtil.formatDate(DateUtil.DATE_FORMAT, currentDate)));
					suffix.append(String.format("'%s'", DateUtil.formatDate(DateUtil.DATE_FORMAT, currentDate)));
					suffix.append("),");
				}  

				String sql = sqlPrefixBuffer.toString() + suffix.substring(0, suffix.length() - 1);  
				pst.addBatch(sql);   // 添加执行sql  
				pst.executeBatch();  // 执行操作  
				conn.commit();   // 提交事务  
				suffix = new StringBuffer();   // 清空上一次添加的数据  
			}  
			pst.close();  
		} catch (SQLException e) {  
			result = false;
			try {  
				conn.rollback(); //进行事务回滚  
			} catch (SQLException ex) {   
			}
			e.printStackTrace();  
		} finally{ 
			try {
				conn.close();  //关闭连接
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("!!!!!!!!!!!close onnection fail!!!!!!!!!!!!!!!!");
			}
		}

		Long end = new Date().getTime();  
		// 耗时  
		log.debug(String.format("batchInsertUserCoupon==>>[rows: %s, time: %sms]", commitPerCount, end - begin));  
		return result;
	}

	@Override
	public List<UserCoupon> getUserCouponListByCreateTime(Date beginDate, Date endDate, Long userId) {
		return userCouponRepository.getUserCouponListByCreateTime(beginDate, endDate, userId);
	}

	/**
	 * 进官方微信送红包
	 */
	@Override
	public boolean intoWeiXinFlockGiveCoupon(UserInfo userInfo) {
		try{
			CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_WEIXIN_FLOCK);
			if (task != null && task.getTaskId() != null) {
				MsgModel<UserCoupon> msgModel = this.saveUserCoupon(task, userInfo.getUserId(), false);
				if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
					// 发放优惠券成功
					return true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 活动投票送礼包
	 */
	@Override
	public boolean voteActivityGiveCoupon(UserInfo userInfo, CouponTask task) {
		if (task != null && task.getTaskId() != null) {
			MsgModel<UserCoupon> msgModel = this.saveUserCoupon(task, userInfo.getUserId(), false);
			if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
				// 发放优惠券成功
				CouponTask dbtask = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_VOTE_JOIN);
				if (dbtask != null && task.getTaskId() != null) {
					this.saveUserCoupon(dbtask, userInfo.getUserId(), false);
				}
				return true;
			}
		}
		return false;
	}

	
	/**
	 * vip邀请用户送礼包
	 */
	@Override
	public void inviteVipGiveCoupon(UserInfo userInfo) {
		CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_INVITE_VIP);
		if (task != null && task.getTaskId() != null) {
			this.saveUserCoupon(task, userInfo.getUserId(), true);
		}
	}
	
	
	/**
	 * 报名投票送优惠券
	 */
	@Override
	public void partTakeVoteGiveCoupon(UserInfo userInfo) {
		CouponTask task = Constants.COUPON_TASK_MAP.get(CouponTask.TASK_NAME_ACTIVITY_VOTE);
		if (task != null && task.getTaskId() != null) {
			this.saveUserCoupon(task, userInfo.getUserId(), false);
		}
	}
	
	/**
	 * 充值赠送商品券
	 */
	@Override
	public void rechargeGiveProductCoupon(Long userId,Long productId,Long rechargeTemplateId) {
		// 优惠券发放
		Coupon coupon = this.couponManager.getCouponByRechargeTemplateId(rechargeTemplateId,StringUtil.null2Str(productId));
		if(coupon != null && coupon.getCouponId() != null){
			Product product = productManager.get(productId);
			String imagePath = "";
			if(product != null && product.getProductId() != null) {
				imagePath = product.getImage();
			}
			String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
			String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, new Date());
			UserCoupon userCoupon = new UserCoupon();
			userCoupon.setCouponNo(CoreInitUtil.getRandomNo());
			userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
			userCoupon.setReceiveTime(receiveTime);
			userCoupon.setEffectiveTime(effectiveTime);
			userCoupon.setUserId(userId);
			userCoupon.setCouponId(coupon.getCouponId());
			userCoupon.setProductImagePath(imagePath);
			userCoupon.setIsGiftCoupon(StringUtil.nullToBoolean(coupon.getIsGiftCoupon()));
			userCoupon.setIsRechargeProductCoupon(true);
			userCoupon.setProductId(productId);
			userCoupon.setIsShowGet(true);
			userCoupon.setCreateTime(DateUtil.getCurrentDate());
			userCoupon.setUpdateTime(userCoupon.getCreateTime());
			userCoupon = this.userCouponRepository.save(userCoupon);
		}
	}

	@Transactional
	@Override
	public void sendMemberCoupon(List<Long> couponIdList,Long userId) {
		if(couponIdList != null && !couponIdList.isEmpty()) {
			String receiveTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, new Date());
			List<UserCoupon> userCouponList = new ArrayList<UserCoupon>();
			for(Long couponId : couponIdList) {
				Coupon coupon = Constants.COUPON_MAP.get(couponId);
				if(coupon == null || coupon.getCouponId() == null) {
					coupon = this.couponManager.get(StringUtil.nullToLong(couponId));
				}
				if(coupon != null && coupon.getCouponId() != null) {
					String effectiveTime = DateUtil.getNearlyDate(StringUtil.nullToInteger(coupon.getEffectiveTime()));
					UserCoupon userCoupon = new UserCoupon();
					userCoupon.setCouponId(couponId);
					userCoupon.setReceiveTime(receiveTime);
					userCoupon.setEffectiveTime(effectiveTime);
					userCoupon.setIsGiftCoupon(StringUtil.nullToBoolean(coupon.getIsGiftCoupon()));
					userCoupon.setCouponNo(CoreUtil.getUUID());
					userCoupon.setCouponStatus(UserCouponStatus.USER_COUPON_STATUS_NOT_USED);
					userCoupon.setCreateTime(DateUtil.getCurrentDate());
					userCoupon.setUpdateTime(userCoupon.getCreateTime());
					userCoupon.setUserId(userId);
					userCouponList.add(userCoupon);
				}
			}
			this.batchInsert(userCouponList, userCouponList.size());
		}
	}

	@Override
	public void updateUserCouponStatus(int status,List<Long> userCouponIdList) {
		this.userCouponRepository.updateUserCouponStatus(status,userCouponIdList);
	}
}
