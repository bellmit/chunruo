package com.chunruo.webapp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.Bank;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.repository.ProductRepository;
import com.chunruo.core.repository.RefundRepository;
import com.chunruo.core.service.BankManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.ProductWarehouseManager;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.vo.OrderExportVo;

public class OrderUtil {

	/**
	 * 读取订单的相关信息
	 * @param orderList
	 * @param isIntercept
	 * @return
	 */
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
			
			// 订单用户信息
			Map<Long, UserInfo> userInfoIdMap = new HashMap<Long, UserInfo>();
			List<UserInfo> userList = userInfoManager.getByIdList(StringUtil.longSetToList(userIdSet));
			for (UserInfo userInfo : userList) {
				Long userId = userInfo.getUserId();
				userInfoIdMap.put(userId, userInfo);
			}
			
			// 订单上级店铺信息
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

			// 订单信息补全
			for (Order o1 : orderList) {
				// 支付收款账号
				if(StringUtil.nullToBoolean(o1.getIsPaymentSucc())){
					if(StringUtil.compareObject(PaymentType.PAYMENT_TYPE_WECHAT, o1.getPaymentType())){
						//微信支付
						if(Constants.WECHAT_CONFIG_ID_MAP.containsKey(StringUtil.nullToLong(o1.getWeChatConfigId()))){
							WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(o1.getWeChatConfigId()));
							o1.setAcceptPayName(StringUtil.null2Str(weChatAppConfig.getAcceptPayName()));
						}
					}else if(StringUtil.compareObject(PaymentType.PAYMENT_TYPE_ALIPAY, o1.getPaymentType())){
						//支付宝支付
						o1.setAcceptPayName("支付宝V2.0");
					}
				}

				// 订单归属店铺
				ProductWarehouse warehouse = Constants.PRODUCT_WAREHOUSE_MAP.get(StringUtil.nullToLong(o1.getWareHouseId()));
				if (warehouse != null && warehouse.getWarehouseId() != null) {
					o1.setWareHouseName(warehouse.getName());
				}

				// 订单归属店铺
				UserInfo userInfo = userInfoIdMap.get(o1.getUserId());
				if (userInfo != null && userInfo.getUserId() != null) {
					o1.setStoreName(userInfo.getStoreName());
					o1.setStoreMobile(userInfo.getMobile());
					o1.setUserName(userInfo.getNickname());
					o1.setUserLevel(StringUtil.nullToInteger(userInfo.getLevel()));
				}

				// 订单归属上级店铺
				UserInfo topUserInfo = topUserInfoIdMap.get(o1.getTopUserId());
				if (topUserInfo != null && topUserInfo.getUserId() != null) {
					o1.setTopStoreName(topUserInfo.getStoreName());
				}
				
				RefundReason refundReason = Constants.REFUND_REASON_MAP.get(StringUtil.nullToLong(o1.getCancelReasonId()));
                if(refundReason != null && refundReason.getReasonId() != null) {
                	o1.setCancelReason(StringUtil.null2Str(refundReason.getReason()));
                }
                
                //订单退款状态
                Refund refund = refundMap.get(o1.getOrderId());
                if(refund != null && refund.getRefundId() != null) {
                	o1.setRefundStatus(refund.getRefundStatus());
                }
				// 订单省\市\区信息
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

	/**
	 * 财务订单导出
	 * 
	 * @param beginTime
	 * @param endTime
	 * @param mobile
	 * @param status
	 * @param couponId
	 * @return
	 */
	public static List<Object[]> orderReport(String beginTime, String endTime, String mobile, String status, Long couponId) {
		List<Object[]> mapList = new ArrayList<Object[]> ();
		try{
			OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
			ProductWarehouseManager productWarehouseManager = Constants.ctx.getBean(ProductWarehouseManager.class); 
			// 检查时间是否有效
			boolean isEffectiveTime = true;
			if(!DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(beginTime))
					|| !DateUtil.isEffectiveTime(DateUtil.dateFormat, StringUtil.null2Str(endTime))){
				isEffectiveTime = false;
			}
			Long mobileUserId = null;
			List<Long> firstTeamUserIdList = new ArrayList<Long>();
			List<Object[]> orderList = new ArrayList<Object[]>();

	        StringBuffer sqlxBuffer = new StringBuffer ();
	        sqlxBuffer.append("select jo.order_id,jo.order_no,jo.trade_no,joi.product_name,joi.product_tags,joi.product_code,joi.product_id,joi.quantity,joi.price,jo.is_share_buy,jo.profit_top,jo.profit_sub,jo.share_user_id,");
	        sqlxBuffer.append("jo.user_id,jo.status,jo.consignee,jo.consignee_phone,jo.payment_type,");
	        sqlxBuffer.append("DATE_FORMAT(jo.create_time,'%Y-%m-%d %H:%i:%s'),DATE_FORMAT(jo.pay_time,'%Y-%m-%d %H:%i:%s'), DATE_FORMAT(jo.sent_time,'%Y-%m-%d %H:%i:%s'),");
	        sqlxBuffer.append("DATE_FORMAT(jo.delivery_time,'%Y-%m-%d %H:%i:%s'), DATE_FORMAT(jo.cancel_time,'%Y-%m-%d %H:%i:%s'), DATE_FORMAT(jo.complate_time,'%Y-%m-%d %H:%i:%s'),");
	        sqlxBuffer.append("DATE_FORMAT(jo.refund_time,'%Y-%m-%d %H:%i:%s'),");
	        sqlxBuffer.append("jo.pay_money,jo.cancel_method,jo.is_check,joi.price as productPrice,jo.order_amount,jo.preferential_amount,jo.province_id,jo.city_id,jo.area_id,");
	        sqlxBuffer.append("jo.top_user_id,jo.address,jo.ware_house_id ");
	        sqlxBuffer.append(" from jkd_order_items joi,jkd_order jo where ((jo.is_sub_order = 1 and joi.sub_order_id = jo.order_id) or (jo.is_split_single = 0 and joi.order_id = jo.order_id)) ");
			
	        // 通过手机号码查询订单信息
			if(!StringUtil.isNull(mobile) && StringUtil.isValidateMobile(mobile)){
				// 时间无效
				if(!isEffectiveTime){
					return mapList;
				}
				List<Object[]> userList = new ArrayList<Object[]>();
				if(StringUtil.compareObject(StringUtil.nullToInteger(status), 5)) {
					// 三级导出 (用户A,用户A直属下线,下线的下线) PS:原团队导出
					String sql_1 = " SELECT user_id,level FROM jkd_user_info WHERE is_agent = 1 and mobile = '%s' ";
					List<Object[]> mobileUser = userInfoManager.querySql(String.format(sql_1,mobile));
					if(mobileUser != null && mobileUser.size() >0) {
						userList.addAll(mobileUser);
						mobileUserId = StringUtil.nullToLong(mobileUser.get(0)[0]);
					}
					String sql_2 = " SELECT user_id,level FROM jkd_user_info WHERE is_agent = 1 and top_user_id IN (SELECT user_id FROM jkd_user_info WHERE is_agent = 1 and mobile = '%s') ";
					List<Object[]> firstTeamUserList = userInfoManager.querySql(String.format(sql_2,mobile));
					if(firstTeamUserList != null && firstTeamUserList.size() >0) {
						userList.addAll(firstTeamUserList);
						for (Object[] objects : firstTeamUserList) {
							firstTeamUserIdList.add(StringUtil.nullToLong(objects[0]));
						}
					}
					String sql_3 = " SELECT user_id,level FROM jkd_user_info WHERE is_agent = 1 and top_user_id IN (SELECT user_id FROM jkd_user_info WHERE is_agent = 1 and top_user_id IN (SELECT user_id FROM jkd_user_info WHERE is_agent = 1 and mobile = '%s')) ";
					List<Object[]> secondTeamUserList = userInfoManager.querySql(String.format(sql_3,mobile));
					if(secondTeamUserList != null && secondTeamUserList.size() >0) {
						userList.addAll(secondTeamUserList);
					}
				}else if(StringUtil.compareObject(StringUtil.nullToInteger(status), 1)) {
					// 二级导出 (用户A,用户A直属下线) 	     PS:原财务导出
					String sql_1 = " SELECT user_id,level FROM jkd_user_info WHERE is_agent = 1 and mobile = '%s' ";
					List<Object[]> mobileUser = userInfoManager.querySql(String.format(sql_1,mobile));
					if(mobileUser != null && mobileUser.size() >0) {
						userList.addAll(mobileUser);
						mobileUserId = StringUtil.nullToLong(mobileUser.get(0)[0]);
					}
					String sql_2 = " SELECT user_id,level FROM jkd_user_info WHERE is_agent = 1 and top_user_id IN (SELECT user_id FROM jkd_user_info WHERE is_agent = 1 and mobile = '%s') ";
					List<Object[]> firstTeamUserList = userInfoManager.querySql(String.format(sql_2,mobile));
					if(firstTeamUserList != null && firstTeamUserList.size() >0) {
						userList.addAll(firstTeamUserList);
						for (Object[] objects : firstTeamUserList) {
							firstTeamUserIdList.add(StringUtil.nullToLong(objects[0]));
						}
					}
				}
				if(userList != null && userList.size() > 0){
					List<Long> userIdList = new ArrayList<Long>();
					for (Object[] userObject : userList) {
						Long userId = StringUtil.nullToLong(userObject[0].toString());
						if(!userIdList.contains(userId)) {
							userIdList.add(userId);
						}
					}
					if (userIdList.size() < 1000) {
						sqlxBuffer.append(" and (DATE_FORMAT(jo.create_time,'%Y-%m-%d %H:%i:%s') BETWEEN '" + beginTime + "' and '" + endTime + "') and jo.store_id in(" + StringUtil.longListToStr(userIdList) + ") order by jo.create_time desc");
						orderList = orderManager.querySql(sqlxBuffer.toString());
					} else {
					    int len = 1000;
					    int size = userIdList.size();
					    int count = (size + len - 1) / len;
					    for (int i = 0; i < count; i++) {
					    	StringBuffer sqlxBuffer_1 = new StringBuffer(sqlxBuffer.toString());
					        List<Long> subList = userIdList.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
					        sqlxBuffer_1.append(" and (DATE_FORMAT(jo.create_time,'%Y-%m-%d %H:%i:%s') BETWEEN '" + beginTime + "' and '" + endTime + "') and jo.store_id in(" + StringUtil.longListToStr(subList) + ") order by jo.create_time desc");
					        List<Object[]> result = orderManager.querySql(sqlxBuffer_1.toString());
					        orderList.addAll(result);
					    }
					}
				}
			}else {
				if(StringUtil.compareObject(StringUtil.nullToInteger(status), 1)
						|| StringUtil.compareObject(StringUtil.nullToInteger(status), 5)
						|| StringUtil.compareObject(StringUtil.nullToInteger(status), 6)){
					// 时间无效
					if(!isEffectiveTime){
						return mapList;
					}
					
					if(StringUtil.compareObject(StringUtil.nullToInteger(status), 6)) {
						//待出库订单导出
						sqlxBuffer.append(" and jo.is_split_single = false and is_push_erp = true and status = 2 and is_intercept = false ");
					}
					// 按时间段查询订单记录
					sqlxBuffer.append(" and (DATE_FORMAT(jo.create_time,'%Y-%m-%d %H:%i:%s') BETWEEN '" + beginTime + "' and '" + endTime + "')  order by jo.create_time desc");
					orderList = orderManager.querySql(sqlxBuffer.toString());
				}else if(StringUtil.compareObject(StringUtil.nullToInteger(status), 2)){
					// 异常订单记录
					sqlxBuffer.append(" and hour(timediff(now(), jo.pay_time)) > 72 and jo.status = 2 order by jo.create_time desc");
					orderList = orderManager.querySql(sqlxBuffer.toString());
				}else if(StringUtil.compareObject(StringUtil.nullToInteger(status), 3)){
					// 优惠券订单记录
					sqlxBuffer.append(String.format(" and jo.user_coupon_Id in(select user_coupon_id from jkd_user_coupon where coupon_id = %s) order by jo.create_time desc", StringUtil.nullToLong(couponId)));
					orderList = orderManager.querySql(sqlxBuffer.toString());
				}
			}

			// 订单Map集合
			if (orderList != null && orderList.size() > 0) {
				Set<Long> userIdSet = new HashSet<Long> ();
				Set<Long> orderIdSet = new HashSet<Long> ();
				Set<Long> productIdSet = new HashSet<Long> ();
				List<OrderExportVo> orderExportList = new ArrayList<OrderExportVo> (); 
				for (Object[] object : orderList) {
				try {
					OrderExportVo orderExport = new OrderExportVo ();
					orderExport.setOrderId(StringUtil.nullToLong(object[0]));
					orderExport.setOrderNo(StringUtil.null2Str(object[1]));
					orderExport.setTradeNo(StringUtil.null2Str(object[2]));
					orderExport.setProductName(StringUtil.null2Str(object[3]));
					orderExport.setProductTags(StringUtil.null2Str(object[4]));
					orderExport.setProductCode(StringUtil.null2Str(object[5]));
					orderExport.setProductId(StringUtil.nullToLong(object[6]));
					orderExport.setQuantity(StringUtil.nullToInteger(object[7]));
					orderExport.setProductPrice(StringUtil.nullToDoubleFormat(object[8]));
					orderExport.setIsShareBuy(StringUtil.nullToBoolean(object[9]));
					orderExport.setProfitTop(StringUtil.nullToDoubleFormat(object[10]));
					orderExport.setProfitSub(StringUtil.nullToDoubleFormat(object[11]));
					orderExport.setShareUserId(StringUtil.nullToLong(object[12]));
					orderExport.setUserId(StringUtil.nullToLong(object[13]));
					orderExport.setStatus(StringUtil.nullToInteger(object[14]));
					orderExport.setConsignee(StringUtil.null2Str(object[15]));
					orderExport.setConsigneePhone(StringUtil.null2Str(object[16]));
					orderExport.setPaymentType(StringUtil.nullToInteger(object[17]));
					orderExport.setCreateTime(StringUtil.null2Str(object[18]));
					orderExport.setPayTime(StringUtil.null2Str(object[19]));
					orderExport.setSentTime(StringUtil.null2Str(object[20]));
					orderExport.setDeliveryTime(StringUtil.null2Str(object[21]));
					orderExport.setCancelTime(StringUtil.null2Str(object[22]));
					orderExport.setComplateTime(StringUtil.null2Str(object[23]));
					orderExport.setRefundTime(StringUtil.null2Str(object[24]));
					orderExport.setPayMoney(StringUtil.nullToDoubleFormat(object[25]));
					orderExport.setCancelMethod(StringUtil.nullToInteger(object[26]));
					orderExport.setIsCheck(StringUtil.nullToBoolean(object[27]));
					orderExport.setProductPrice(StringUtil.nullToDoubleFormat(object[28]));
					orderExport.setOrderAmount(StringUtil.nullToDoubleFormat(object[29]));
					orderExport.setPreferentialAmount(StringUtil.nullToDoubleFormat(object[30]));
					orderExport.setProvinceId(StringUtil.nullToLong(object[31]));
					orderExport.setCityId(StringUtil.nullToLong(object[32]));
					orderExport.setAreaId(StringUtil.nullToLong(object[33]));
					orderExport.setTopUserId(StringUtil.nullToLong(object[34]));
					orderExport.setAddress(StringUtil.null2Str(object[35]));
					orderExport.setWareHouseId(StringUtil.nullToLong(object[36]));
					orderExportList.add(orderExport);
					// 用户ID
					userIdSet.add(orderExport.getUserId());
					userIdSet.add(orderExport.getTopUserId());
					productIdSet.add(orderExport.getProductId());
					
					// 订单有发货记录
					if(orderExport.getSentTime() != null && DateUtil.isEffectiveTime(DateUtil.dateFormat, orderExport.getSentTime())){
						orderIdSet.add(orderExport.getOrderId());	
					}
					}catch(Exception e) {
						e.printStackTrace();
						continue;
					}
			  }
				
				if(orderExportList != null && orderExportList.size() > 0){
					// 用户信息
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo> ();
					List<Long> userIdList = new ArrayList<Long>();
					userIdList.addAll(userIdSet);
					List<UserInfo> userInfoList = new ArrayList<UserInfo>();
					if (userIdList.size() < 1000) {
						userInfoList = userInfoManager.getByIdList(userIdList);
					} else {
					    int len = 1000;
					    int size = userIdList.size();
					    int count = (size + len - 1) / len;
					    for (int i = 0; i < count; i++) {
					        List<Long> subList = userIdList.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
					        List<UserInfo> result = userInfoManager.getByIdList(subList);
					        userInfoList.addAll(result);
					    }
					}
					if(userInfoList != null && userInfoList.size() > 0){
						for(UserInfo userInfo : userInfoList){
							userInfoMap.put(userInfo.getUserId(), userInfo);
						}
					}
					
					// 商品分类
					Map<String, String> categoryByProductIdMap = new HashMap<String, String> ();
					if(productIdSet != null && productIdSet.size() > 0){
						StringBuffer sqlBuffer = new StringBuffer ("select CONCAT(pw.product_id, '_', pc.level), pc.name from jkd_product pw, jkd_product_category pc");
						sqlBuffer.append(" where pw.product_id in(%s) and pw.category_fid = pc.category_id or pw.category_id = pc.category_id");
						List<Object[]> object2List = orderManager.querySql(String.format(sqlBuffer.toString(), StringUtil.longSetToStr(productIdSet)));
						for(Object[] object : object2List){
							categoryByProductIdMap.put(StringUtil.null2Str(object[0]), StringUtil.null2Str(object[1]));
						}
					}
					
					// 物流信息
					Map<Long, String> packageByOrderIdMap = new HashMap<Long, String> ();
					if(orderIdSet != null && orderIdSet.size() > 0){
						String sql = "select order_id, express_no, express_company from jkd_order_package where order_id in (%s)";
						List<Object[]> object2List = orderManager.querySql(String.format(sql, StringUtil.longSetToStr(orderIdSet)));
						for(Object[] object : object2List){
							Long orderId = StringUtil.nullToLong(object[0]);
							if(packageByOrderIdMap.containsKey(orderId)){
								String expressInfo = String.format("%s|%s", StringUtil.null2Str(object[1]), StringUtil.null2Str(object[2]));
								packageByOrderIdMap.put(orderId, String.format("%s,%s", packageByOrderIdMap.get(orderId), expressInfo));
							}else{
								String expressInfo = String.format("%s|%s", StringUtil.null2Str(object[1]), StringUtil.null2Str(object[2]));
								packageByOrderIdMap.put(orderId, expressInfo);
							}
						}
					}
					
					//仓库类型
					Map<Long,ProductWarehouse> wareHouseMap = new HashMap<Long,ProductWarehouse>();
					List<ProductWarehouse> wareHouseList = productWarehouseManager.getAll();
					if(wareHouseList != null && wareHouseList.size() > 0) {
						for(ProductWarehouse house : wareHouseList) {
							wareHouseMap.put(house.getWarehouseId(), house);
						}
					}
					//支付类型
					Map<Integer, String> paymentTypeMap = new HashMap<Integer, String> ();
					paymentTypeMap.put(PaymentType.PAYMENT_TYPE_WECHAT, "微信支付");
					paymentTypeMap.put(PaymentType.PAYMENT_TYPE_ALIPAY, "支付宝支付");
					
					//账号所属用户
					UserInfo mobileUserInfo = new UserInfo();
					if(mobileUserId != null) {
						UserInfo userInfo = userInfoMap.get(mobileUserId);
						if(userInfo != null && userInfo.getUserId() != null) {
							mobileUserInfo = userInfo;
						}
					}
					
					for(OrderExportVo orderExport : orderExportList){
                     try {
						// 当前店铺信息
						String storeName = "";
						String storeLinkman = "";
						String storeMobile = "";
						String storeUserId = "";
						String storeLevel = "";
						String topUserName = "";
						String registerTime = "";
						String warehouseName = "";
						if(userInfoMap != null && userInfoMap.size() > 0){
							// 当前用户信息
							if(userInfoMap.containsKey(orderExport.getUserId())){
								UserInfo userInfo = userInfoMap.get(orderExport.getUserId());
								storeName = StringUtil.null2Str(userInfo.getStoreName());
								storeLinkman = StringUtil.null2Str(userInfo.getLinkman());
								storeMobile = StringUtil.null2Str(userInfo.getStoreMobile());
								storeUserId = StringUtil.null2Str(userInfo.getUserId());
								storeLevel = OrderUtil.userLevel(StringUtil.nullToInteger(userInfo.getLevel()));
								Date registerTimeTemp = userInfo.getRegisterTime();
								registerTime = registerTimeTemp == null ? "" : DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, userInfo.getRegisterTime());
							}
							
							// 上级用户信息
							if(userInfoMap.containsKey(orderExport.getTopUserId())){
								UserInfo userInfo = userInfoMap.get(orderExport.getTopUserId());
								topUserName = StringUtil.null2Str(userInfo.getNickname());
							}
						}
						ProductWarehouse productWarehouse = wareHouseMap.get(orderExport.getWareHouseId());
						if(productWarehouse != null && productWarehouse.getWarehouseId() != null) {
							warehouseName = StringUtil.null2Str(productWarehouse.getName());
						}
						
						List<Object> objectList = new ArrayList<Object> ();
						objectList.add(orderExport.getOrderNo());
						objectList.add(orderExport.getTradeNo());
						objectList.add(orderExport.getProductName());
						objectList.add(orderExport.getProductTags());
						objectList.add(orderExport.getProductCode());
						objectList.add(categoryByProductIdMap.get(String.format("%s_%s", orderExport.getProductId(), 1)));
						objectList.add(categoryByProductIdMap.get(String.format("%s_%s", orderExport.getProductId(), 2)));
						objectList.add(orderExport.getQuantity());
						objectList.add(orderExport.getProductPrice());
						objectList.add(StringUtil.nullToBoolean(orderExport.getIsShareBuy()) ? "分享订单":"自己订单");
						objectList.add(orderExport.getProfitTop());
						objectList.add(orderExport.getProfitSub());
						objectList.add(orderExport.getShareUserId());
						objectList.add(storeLinkman);
						objectList.add(Constants.orderStatusMap.get(orderExport.getStatus()));
						objectList.add(orderExport.getConsignee());
						objectList.add(orderExport.getConsigneePhone());
						objectList.add(paymentTypeMap.get(orderExport.getPaymentType()));
						objectList.add(orderExport.getCreateTime());
						objectList.add(orderExport.getPayTime());
						objectList.add(orderExport.getSentTime());
						objectList.add(orderExport.getDeliveryTime());
						objectList.add(orderExport.getCancelTime());
						objectList.add(orderExport.getComplateTime());
						objectList.add(orderExport.getRefundTime());
						objectList.add(orderExport.getPayMoney());
						objectList.add(OrderUtil.cancelType(orderExport.getCancelMethod()));
						objectList.add("");
						objectList.add(storeName);
						objectList.add(storeLinkman);
						objectList.add(storeMobile);
						objectList.add(storeUserId);
						objectList.add(storeLevel);
						objectList.add("IsInviteRebate");
						objectList.add(orderExport.getTopUserId());
						objectList.add(topUserName);
						objectList.add(StringUtil.nullToBoolean(orderExport.getIsCheck()) ? "已对账" : "未对账");
						objectList.add(StringUtil.nullToDoubleFormatStr(orderExport.getQuantity() * orderExport.getProductPrice()));
						objectList.add(StringUtil.nullToDoubleFormatStr(orderExport.getOrderAmount()));
						objectList.add(StringUtil.nullToDoubleFormatStr(orderExport.getPreferentialAmount()));
						objectList.add(packageByOrderIdMap.get(orderExport.getOrderId()));
						objectList.add(orderExport.getAddress());
						objectList.add(Constants.AREA_MAP.get(orderExport.getProvinceId()).getAreaName());
						objectList.add(Constants.AREA_MAP.get(orderExport.getCityId()).getAreaName());
						objectList.add(Constants.AREA_MAP.get(orderExport.getAreaId()).getAreaName());
						objectList.add(registerTime);
						objectList.add(warehouseName);
						
						// 数据行
						mapList.add(objectList.toArray(new Object[objectList.size()]));
					}catch(Exception e) {
						e.printStackTrace();
						continue;
					}
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapList;
	}

	/**
	 * 订单取消方式(0:过期自动取消;1:卖家手动取消;2:买家手动取消)
	 * @param cancelType
	 * @return
	 */
	private static String cancelType(int cancelType) {
		if (cancelType == 0) {
			return "过期自动取消";
		} else if (cancelType == 1) {
			return "卖家手动取消";
		} else if (cancelType == 2) {
			return "买家手动取消";
		}
		return "";
	}
	
	
	/**
	 * 用户等级(普通用户;1:VIP;2:经销商;3:总代)
	 * @param level
	 * @return
	 */
	private static String userLevel(int level) {
		if (level == 1) {
			return "VIP";
		} else if (level == 2) {
			return "经销商";
		} else if (level == 3) {
			return "总代";
		}
		return "普通用户";
	}
	
	/**
	 * 退款单导出
	 * @param beginTime
	 * @param endTime
	 * @param mobile
	 * @return
	 */
	public static List<Object[]> refundReport(String beginTime, String endTime, List<Long> refundIdList) {
		List<Object[]> mapList = new ArrayList<Object[]> ();
		OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
		UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
		RefundRepository refundRepository = Constants.ctx.getBean(RefundRepository.class);
		ProductRepository productRepository = Constants.ctx.getBean(ProductRepository.class);

		List<Long> orderIdList = new ArrayList<>();
		List<Long> orderItemIdList = new ArrayList<>();
		List<Long> productIdList = new ArrayList<>();
		List<Long> userIdList = new ArrayList<>();

		List<Refund> refundList = null;
		if (refundIdList != null && refundIdList.size() > 0) {
			refundList = refundRepository.getByIdList(refundIdList);
		} else {
			Date dateBeginTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, beginTime);
			Date dateEndTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, endTime);
			refundList = refundRepository.getRefundListByTime(dateBeginTime, dateEndTime, true);
		}
		
		// 退货信息
		if (refundList != null && refundList.size() > 0) {
			for (Refund refund : refundList) {
				orderIdList.add(refund.getOrderId());
				orderItemIdList.add(refund.getOrderItemId());
				productIdList.add(refund.getProductId());
				userIdList.add(refund.getUserId());
			}
		} else {
			return mapList;
		}

		// 店铺Map集合
		Map<Long, UserInfo> userInfoMap = new HashMap<>();
		// 订单Map集合
		Map<Long, Order> orderMap = new HashMap<>();
		// 商品Map集合（Product）
		Map<Long, Product> productMap = new HashMap<Long, Product>();

		// 订单信息
		List<Order> orderList = orderManager.getByIdList(orderIdList);
		if (orderList != null && orderList.size() > 0) {
			for (Order order : orderList) {
				orderMap.put(order.getOrderId(), order);
			}
		} else {
			return mapList;
		}
		
		// 店铺信息
		List<UserInfo> userInfoList = userInfoManager.getByIdList(userIdList);
		if (userInfoList != null && userInfoList.size() > 0) {
			for (UserInfo userInfo : userInfoList) {
				userInfoMap.put(userInfo.getUserId(), userInfo);
			}
		} else {
			return mapList;
		}
		
		// 商品信息
		List<Product> productList = productRepository.getByIdList(productIdList);
		if (productList != null && productList.size() > 0) {
			for (Product product : productList) {
				// 商品大类
				String fName = "";
				List<Long> categoryFidList = StringUtil.stringToLongArray(product.getCategoryFids());
				if(categoryFidList != null && !categoryFidList.isEmpty()) {
					StringBuilder categoryFidName = new StringBuilder();
					for(Long categoryFid : categoryFidList) {
						ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryFid);
						if(productCategory != null && productCategory.getCategoryId() != null) {
							categoryFidName.append(StringUtil.null2Str(productCategory.getName()));
							categoryFidName.append(",");
						}
					}
					fName = categoryFidName.toString();
				}
				
				// 商品小类
				String name = "";
				List<Long> categoryIdList = StringUtil.stringToLongArray(product.getCategoryIds());
				if(categoryIdList != null && !categoryIdList.isEmpty()) {
					StringBuilder categoryIdName = new StringBuilder();
					for(Long categoryId : categoryIdList) {
						ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
						if(productCategory != null && productCategory.getCategoryId() != null) {
							categoryIdName.append(StringUtil.null2Str(productCategory.getName()));
							categoryIdName.append(",");
						}
					}
					name = categoryIdName.toString();
				}
				
				product.setCategoryFidName(fName);
				product.setCategoryIdName(name);
				productMap.put(product.getProductId(), product);
			}
		} else {
			return mapList;
		}

		for (Refund refund : refundList) {
			String[] row = new String[9];
			row[0] = orderMap.get(refund.getOrderId()).getOrderNo();
			row[1] = refund.getRefundNumber();
			try {
				row[2] = productMap.get(refund.getProductId()).getName();
			} catch (Exception e) {
				row[2] = "";
			}
			row[3] = Constants.REFUND_REASON_MAP.get(refund.getReasonId()).getReason();
			try {
				row[4] = orderMap.get(refund.getOrderId()).getPaymentType() == 1 ? "支付宝支付" : "微信支付";
			} catch (Exception e) {
				row[4] = "";
			}
			row[5] = userInfoMap.get(refund.getUserId()).getStoreName();
			row[6] = orderMap.get(refund.getOrderId()).getConsignee();
			row[7] = refund.getRefundAmount();
			row[8] = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, refund.getCompletedTime());

			mapList.add(row);
		}

		return mapList;
	}

	
	
}
