package com.chunruo.webapp.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.StringUtil;

public class OrderUtil {

	public static List<Order> getStoreAndUserName(List<Order> orderList, boolean isIntercept) {
		try{
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
            RefundManager refundManager = Constants.ctx.getBean(RefundManager.class);
            
			Set<Long> userIdSet = new HashSet<Long> ();
			Set<Long> topUserIdSet = new HashSet<Long> ();
			Set<Long> orderIdSet = new HashSet<Long>();
			for (Order o : orderList) {
				userIdSet.add(o.getUserId());
				topUserIdSet.add(o.getTopUserId());
				orderIdSet.add(o.getOrderId());
			}
			
			Map<Long, UserInfo> userInfoIdMap = new HashMap<Long, UserInfo>();
			List<UserInfo> userList = userInfoManager.getByIdList(StringUtil.longSetToList(userIdSet));
			for (UserInfo userInfo : userList) {
				Long userId = userInfo.getUserId();
				userInfoIdMap.put(userId, userInfo);
			}
			
			Map<Long, UserInfo> topUserInfoIdMap = new HashMap<Long, UserInfo>();
			List<UserInfo> topUserList = userInfoManager.getByIdList(StringUtil.longSetToList(topUserIdSet));
			for (UserInfo userInfo : topUserList) {
				Long userId = userInfo.getUserId();
				topUserInfoIdMap.put(userId, userInfo);
			}
			
			Map<Long,Refund> refundMap = new HashMap<Long,Refund>();
			List<Refund> refundList = refundManager.getRefundListByOrderIdList(StringUtil.longSetToList(orderIdSet));
			if(refundList != null && !refundList.isEmpty()) {
				for(Refund refund : refundList) {
					refundMap.put(StringUtil.nullToLong(refund.getOrderId()), refund);
				}
			}

			for (Order o1 : orderList) {
				if(StringUtil.nullToBoolean(o1.getIsPaymentSucc())){
					if(StringUtil.compareObject(PaymentType.PAYMENT_TYPE_WECHAT, o1.getPaymentType())){
						if(Constants.WECHAT_CONFIG_ID_MAP.containsKey(StringUtil.nullToLong(o1.getWeChatConfigId()))){
							WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(o1.getWeChatConfigId()));
							o1.setAcceptPayName(StringUtil.null2Str(weChatAppConfig.getAcceptPayName()));
						}
					}
				}


				UserInfo userInfo = userInfoIdMap.get(o1.getUserId());
				if (userInfo != null && userInfo.getUserId() != null) {
					o1.setStoreName(userInfo.getStoreName());
					o1.setStoreMobile(userInfo.getMobile());
					o1.setUserName(userInfo.getNickname());
					o1.setUserLevel(StringUtil.nullToInteger(userInfo.getLevel()));
				}

				UserInfo topUserInfo = topUserInfoIdMap.get(o1.getTopUserId());
				if (topUserInfo != null && topUserInfo.getUserId() != null) {
					o1.setTopStoreName(topUserInfo.getStoreName());
				}
				
				RefundReason refundReason = Constants.REFUND_REASON_MAP.get(StringUtil.nullToLong(o1.getCancelReasonId()));
                if(refundReason != null && refundReason.getReasonId() != null) {
                	o1.setCancelReason(StringUtil.null2Str(refundReason.getReason()));
                }
                
                Refund refund = refundMap.get(o1.getOrderId());
                if(refund != null && refund.getRefundId() != null) {
                	o1.setRefundStatus(refund.getRefundStatus());
                }
				Long provinceId = o1.getProvinceId();
				Long cityId = o1.getCityId();
				Long areaId = o1.getAreaId();
				Area province = Constants.AREA_MAP.get(provinceId);
				if (province != null && province.getAreaId() != null) {
					o1.setProvince(province.getAreaName());
				}
				Area city = Constants.AREA_MAP.get(cityId);
				if (city != null && city.getAreaId() != null) {
					o1.setCity(city.getAreaName());
				}
				Area area = Constants.AREA_MAP.get(areaId);
				if (area != null && area.getAreaId() != null) {
					o1.setCityarea(area.getAreaName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return orderList;
	}


	
	
}
