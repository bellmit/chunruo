package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.RefundListByStoreIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 用户信息
 * @author chunruo
 */
public class UserInfoTag extends BaseTag {
	
	public TagModel<Map<String, Object>> getData(){
		TagModel<Map<String, Object>> tagModel = new TagModel<Map<String, Object>>();
		try {
			// 检查用户是否登录
			UserInfo user = PortalUtil.getCurrentUserInfo(request);
			if (user == null || user.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登录");
				return tagModel;
			}

			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);

			UserInfo xuser = userInfoByIdCacheManager.getSession(user.getUserId());
			if (xuser == null || xuser.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登录");
				return tagModel;
			}
			
			// 拷贝用户对象
			UserInfo userInfo = new UserInfo ();
			BeanUtils.copyProperties(userInfo,xuser);


			// 用户头像特殊处理
			String headerImage = StringUtil.null2Str(userInfo.getHeaderImage());
			if (!StringUtil.isNull(headerImage) && !headerImage.startsWith("http://")
					&& !headerImage.startsWith("https://")) {
				headerImage = RequestUtil.getRequestURL(request) + "/upload/" + headerImage;
			}
			userInfo.setHeaderImage(headerImage);


			Map<String, Object> map = new HashMap<String, Object>();
			map.put("user", userInfo);
			
			//上级店铺信息
			UserInfo topUserInfo = userInfoByIdCacheManager.getSession(StringUtil.nullToLong(userInfo.getTopUserId()));
			if(topUserInfo != null && topUserInfo.getUserId() != null) {
				userInfo.setTopStoreName(StringUtil.null2Str(topUserInfo.getNickname()));
			}
			
			// 普通用户无店铺信息
			map.put("orderInfo", this.getOrderCountInfoByUserId(userInfo));
			
			//用户隐私协议
			tagModel.setData(map);
			tagModel.setCode(PortalConstants.CODE_SUCCESS);
			tagModel.setMsg("请求成功");
			return tagModel;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		tagModel.setCode(PortalConstants.CODE_ERROR);
		tagModel.setMsg("请求失败");
		return tagModel;
	}
	
	/**
	 * 当前店铺的订单状态数量(未付款、待发货、已发货、退款/售后)
	 * @param userId
	 * @param storeId
	 * @return
	 */
	private Map<String,Object> getOrderCountInfoByUserId(UserInfo userInfo){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{		
			OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
			RefundListByStoreIdCacheManager refundListByStoreIdCacheManager = Constants.ctx.getBean(RefundListByStoreIdCacheManager.class);			
			
			int unpayCount = 0;      	        //未付款
			int unsentCount = 0;     	        //未发货
			int undeliveryCount = 0;	        //待收货
			int refuntCount = 0; 		        //退款退货
			
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS);
			statusList.add(OrderStatus.DELIVER_ORDER_STATUS);
			statusList.add(OrderStatus.OVER_ORDER_STATUS);
			
			List<Order> orderList = orderListByStoreIdCacheManager.getSession(userInfo.getUserId());
			if(orderList != null && orderList.size() > 0){
				for(Order order : orderList){
					if(OrderStatus.NEW_ORDER_STATUS.compareTo(order.getStatus()) == 0){
						if(StringUtil.nullToBoolean(order.getIsInvitationAgent())) {
							continue;
						}
						unpayCount ++;
					}else if(statusList.contains(order.getStatus())){
						if(OrderStatus.UN_DELIVER_ORDER_STATUS.compareTo(order.getStatus()) == 0){
							unsentCount ++;
						}else if(OrderStatus.DELIVER_ORDER_STATUS.compareTo(order.getStatus()) == 0){
							undeliveryCount ++;
						}
					}
				}
			}
			
		 	
			// 退货订单列表
			List<Integer> refundStatusList = new ArrayList<>();
			refundStatusList.add(Refund.REFUND_STATUS_WAIT);
			refundStatusList.add(Refund.REFUND_STATUS_SUCCESS);
			refundStatusList.add(Refund.REFUND_STATUS_RECEIPT);
			
			Map<String,Refund> refundMap = refundListByStoreIdCacheManager.getSession(userInfo.getUserId());
			if(refundMap != null && refundMap.size() > 0){
				for(Entry<String, Refund> map : refundMap.entrySet()){
					Refund refund = map.getValue();
					if(refundStatusList.contains(refund.getRefundStatus())){
						refuntCount ++;
					}
				}
			}
			resultMap.put("unpayCount", unpayCount);
			resultMap.put("unsentCount", unsentCount);
			resultMap.put("refuntCount", refuntCount);
			resultMap.put("undeliveryCount", undeliveryCount);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	
	
	/**
	 * 获取用户头像
	 * @param headerImage
	 * @param request
	 * @return
	 */
  public static String getUserHeaderImage(String headerImage,HttpServletRequest request) {
		try {
			if (!StringUtil.isNull(headerImage)
					&& (!headerImage.startsWith("http://") && !headerImage.startsWith("https://"))) {
				headerImage = RequestUtil.getRequestURL(request) + "/upload/" + headerImage;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headerImage;
  }
	
  

  
}
