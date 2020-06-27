package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.OrderPackageManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.WxSendUtil;
import com.chunruo.core.vo.TModel;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.interceptor.AuthorizeInterceptor;
import com.chunruo.webapp.util.OrderUtil;

@Controller
@RequestMapping("/order/")
public class OrderController extends BaseController {
	public final static String XLS_FILE_NAME = ".xls";
	public final static String ZIP_FILE_NAME = ".zip";
	public final static String ID_PATH_PREFIX = "/data/file";
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private OrderPackageManager orderPackageManager;
	@Autowired
	private OrderHistoryManager orderHistoryManager;
    @Autowired
    private ProductManager productManager;
    
	/**
	 * 所有订单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (StringUtil.nullToBoolean(request.getParameter("isExporter"))) {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("success", true);
				resultMap.put("filePath", tmodel.getFilePath());
				return resultMap;
			} else {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("data", tmodel.gettModel());
				resultMap.put("totalCount", tmodel.getCount());
				resultMap.put("filters", tmodel.getFiltersMap());
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 客审订单列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderExamineList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderErpUnpushList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("isSplitSingle", false); 	// 非拆单订单
			paramMap.put("isPushErp", false); 		// 没有推送ERP
			paramMap.put("status", OrderStatus.UN_DELIVER_ORDER_STATUS); // 未发货
			paramMap.put("isIntercept", false); // 非拦截状态
			paramMap.put("isNeedCheckPayment", false); // 是否支付实名认证

			if (StringUtil.nullToBoolean(request.getParameter("isExporter"))) {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("success", true);
				resultMap.put("filePath", tmodel.getFilePath());
				return resultMap;
			} else {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("data", tmodel.gettModel());
				resultMap.put("totalCount", tmodel.getCount());
				resultMap.put("filters", tmodel.getFiltersMap());
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 待出库列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderErpPushList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderErpPushList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("isSplitSingle", false); // 非拆单订单
			paramMap.put("isPushErp", true); // 已推送ERP
			paramMap.put("status", OrderStatus.UN_DELIVER_ORDER_STATUS); // 未发货
			paramMap.put("isIntercept", false); // 非拦截状态

			if (StringUtil.nullToBoolean(request.getParameter("isExporter"))) {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("success", true);
				resultMap.put("filePath", tmodel.getFilePath());
				return resultMap;
			} else {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("data", tmodel.gettModel());
				resultMap.put("totalCount", tmodel.getCount());
				resultMap.put("filters", tmodel.getFiltersMap());
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 已出库列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderErpWarehouseList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderErpWarehouseList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("isSplitSingle", false); // 非拆单订单
			paramMap.put("isPushErp", true); // 已推送ERP
			paramMap.put("status", OrderStatus.DELIVER_ORDER_STATUS); // 已发货
			paramMap.put("isIntercept", false); // 非拦截状态

			if (StringUtil.nullToBoolean(request.getParameter("isExporter"))) {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("success", true);
				resultMap.put("filePath", tmodel.getFilePath());
				return resultMap;
			} else {
				TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
				resultMap.put("data", tmodel.gettModel());
				resultMap.put("totalCount", tmodel.getCount());
				resultMap.put("filters", tmodel.getFiltersMap());
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}


	public TModel<List<Order>> getOrderDataList(Map<String, Object> paramMap, final HttpServletRequest request) {
		int start = StringUtil.nullToInteger(request.getParameter("start"));
		int limit = StringUtil.nullToInteger(request.getParameter("limit"));
		String sort = StringUtil.nullToString(request.getParameter("sort"));
		String filters = StringUtil.nullToString(request.getParameter("filters"));
		Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
		Map<String, Object> filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Order.class);

		Long count = 0L;
		boolean isIntercept = false;
		String xlsFilePath = null;
		List<Order> orderList = new ArrayList<Order>();
		try {
			// 拦截列表查询
			if (paramMap.containsKey("isIntercept")) {
				isIntercept = true;
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				// 关键字过滤虚拟拦截状态
				if (filtersMap.containsKey("status")
						&& StringUtil.compareObject(filtersMap.get("status"), OrderStatus.INTERCEPT_ORDER_STATUS)) {
					// 虚拟拦截状态
					filtersMap.remove("status");
					filtersMap.put("isIntercept", true);
				}
				paramMap.putAll(filtersMap);

			}

			count = this.orderManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				orderList = this.orderManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if (orderList != null && orderList.size() > 0) {
					//检查当前entity是否为托管状态（Managed），若为托管态，需要转变为游离态，这样entity的数据发生改变时，就不会自动同步到数据库中
					//游离态的entity需要调用merge方法转为托管态。
					this.orderManager.detach(orderList);
					for (Order o : orderList) {
						o.setIdentityName(null);
						o.setIdentityNo(null);
						o.setConsigneePhone(StringUtil.mobileFormat(o.getConsigneePhone()));
					}
					orderList = OrderUtil.getStoreAndUserName(orderList, isIntercept);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TModel<List<Order>> tModel = new TModel<List<Order>>();
		tModel.setIsSucc(true);
		tModel.setCount(count);
		tModel.settModel(orderList);
		tModel.setFiltersMap(filtersMap);
		tModel.setFilePath(xlsFilePath);
		return tModel;
	}

	/**
	 * 订单详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getOrderById")
	public @ResponseBody Map<String, Object> getOrderById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		Order order = new Order();
		List<OrderItems> orderItemList = new ArrayList<OrderItems>();
		List<OrderHistory> orderHistoryList = new ArrayList<OrderHistory>();
		List<OrderPackage> orderPackageList = new ArrayList<OrderPackage>();
		try {
			order = this.orderManager.get(orderId);
			if (order != null && order.getOrderId() != null) {
				if (StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
					if (StringUtil.compareObject(PaymentType.PAYMENT_TYPE_WECHAT, order.getPaymentType())) {
						// 微信支付
						if (Constants.WECHAT_CONFIG_ID_MAP.containsKey(StringUtil.nullToLong(order.getWeChatConfigId()))) {
							WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(order.getWeChatConfigId()));
							order.setAcceptPayName(StringUtil.null2Str(weChatAppConfig.getAcceptPayName()));
						}
					}
				}

				// 订单归属店铺
				UserInfo userInfo = this.userInfoManager.get(order.getUserId());
				if (userInfo != null && userInfo.getUserId() != null) {
					order.setStoreName(userInfo.getStoreName());
					// 订单购买人信息
					order.setUserName(userInfo.getNickname());
				}

				// 订单归属上级店铺
				UserInfo topUserInfo = this.userInfoManager.get(order.getTopUserId());
				if (topUserInfo != null && topUserInfo.getUserId() != null) {
					order.setTopStoreName(topUserInfo.getStoreName());
				}

				// 订单省\市\区信息
				Long provinceId = order.getProvinceId();
				Long cityId = order.getCityId();
				Long areaId = order.getAreaId();
				Area province = Constants.AREA_MAP.get(provinceId);
				if (province != null && province.getAreaId() != null) {
					order.setProvince(province.getAreaName());
					Area city = Constants.AREA_MAP.get(cityId);
					if (city != null && city.getAreaId() != null) {
						order.setCity(city.getAreaName());
						Area area = Constants.AREA_MAP.get(areaId);
						if (area != null && area.getAreaId() != null) {
							order.setCityarea(area.getAreaName());
						}
					}
				}
				
				// 检查是否子订单
				if (StringUtil.nullToBoolean(order.getIsSubOrder())) {
					// 子订单商品列表
					orderItemList = this.orderItemsManager.getOrderSubItemsListByOrderId(order.getParentOrderId(), order.getOrderId());
					orderPackageList = this.orderPackageManager.getOrderPackageListByOrderId(order.getOrderId());
				} else {
					// 母订单商品列表
					orderItemList = this.orderItemsManager.getOrderItemsListByOrderId(order.getOrderId());
					if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
						if (orderItemList != null && orderItemList.size() > 0) {
							Set<Long> childOrderIdSet = new HashSet<Long>();
							for (OrderItems item : orderItemList) {
								childOrderIdSet.add(item.getSubOrderId());
							}
							orderPackageList = this.orderPackageManager.getOrderPackageListByOrderIdList(StringUtil.longSetToList(childOrderIdSet));
						}
					} else {
						orderPackageList = this.orderPackageManager.getOrderPackageListByOrderId(order.getOrderId());
					}
				}
				
				// 查找仓库
				if(orderItemList != null && orderItemList.size() > 0){
					List<Long> groupProductIdList =new ArrayList<Long>();
					for(OrderItems orderItems : orderItemList){
						if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())
								&& StringUtil.nullToBoolean(orderItems.getIsMainGroupItem())) {
							groupProductIdList.add(StringUtil.nullToLong(orderItems.getGroupProductId()));
						}
					}
					
					if(groupProductIdList != null && !groupProductIdList.isEmpty()) {
						Map<Long,Product> groupProductMap = new HashMap<Long,Product>();
						List<Product> groupProductList = this.productManager.getByIdList(groupProductIdList);
					    if(groupProductList != null && !groupProductList.isEmpty()) {
					    	for(Product product : groupProductList) {
					    		groupProductMap.put(StringUtil.nullToLong(product.getProductId()), product);
					    	}
					    }
					    
					    for(OrderItems orderItems : orderItemList){
							if(StringUtil.nullToBoolean(orderItems.getIsGroupProduct())) {
								Product product = groupProductMap.get(StringUtil.nullToLong(orderItems.getGroupProductId()));
							    if(product != null && product.getProductId() != null) {
							    	orderItems.setGroupProductName(StringUtil.null2Str(product.getName()));
							    }
							}
						}
					}
				}
				orderHistoryList = this.orderHistoryManager.getOrderHistoryListByOrderId(order.getOrderId());
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("order", order);
		resultMap.put("orderItem", orderItemList);
		resultMap.put("orderHistoryList", orderHistoryList);
		resultMap.put("orderPackageList", orderPackageList);
		return resultMap;
	}

	

	/**
	 * 订单关闭
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderClose")
	public @ResponseBody Map<String, Object> orderClose(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));
		String message = StringUtil.null2Str(request.getParameter("message"));

		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			this.orderManager.updateOrderCloseStatus(idList, message, this.getUserId(request));
			resultMap.put("success", true);
			resultMap.put("error", false);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", true);
		resultMap.put("error", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}

	/**
	 * 获取地区名称
	 * @param areaId
	 * @return
	 */
	public static String getAreaNameById(Long areaId) {
		if (Constants.AREA_MAP.containsKey(areaId)) {
			return StringUtil.null2Str(Constants.AREA_MAP.get(areaId).getAreaName());
		}
		return "";
	}

	/**
	 * 根据用户对象获取全用户信息
	 * @param userAddress
	 * @return
	 */
	public static String getFullAddressInfo(Long provinceId, Long cityId, Long areaId, String address) {
		StringBuffer addressBuffer = new StringBuffer();
		try {
			if (Constants.AREA_MAP.containsKey(provinceId)) {
				addressBuffer.append(Constants.AREA_MAP.get(provinceId).getAreaName());
				if (Constants.AREA_MAP.containsKey(cityId)) {
					addressBuffer.append("/" + Constants.AREA_MAP.get(cityId).getAreaName());
					if (Constants.AREA_MAP.containsKey(areaId)) {
						addressBuffer.append("/" + Constants.AREA_MAP.get(areaId).getAreaName());
						addressBuffer.append("/" + StringUtil.null2Str(address));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressBuffer.toString();
	}

	// 更改订单买家信息
	@RequestMapping(value = "/editBuyertInfo")
	public @ResponseBody Map<String, Object> editBuyerInfo(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		String consigneePhone = StringUtil.nullToString(request.getParameter("consigneePhone"));
		String consignee = StringUtil.nullToString(request.getParameter("consignee"));
		String address = StringUtil.nullToString(request.getParameter("address"));
		if (StringUtil.isNull(orderId)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("ajax.no.record"));
			return resultMap;
		} else if (StringUtil.isNull(address)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("错误,请正确输入收货地址"));
			return resultMap;
		}else if (StringUtil.isNull(consignee) ) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("错误,请正确填写收货人姓名"));
			return resultMap;
		} 
		
		try {
			Order order = this.orderManager.get(orderId);
			if(order != null && order.getOrderId() != null){
				// 检查是否修改手机号码
				String mobile = StringUtil.mobileFormat(order.getConsigneePhone());
				if(!StringUtil.compareObject(mobile, consigneePhone)){
					if (!StringUtil.isValidateMobile(consigneePhone)) {
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("错误,请正确输入电话"));
						return resultMap;
					}
					order.setConsigneePhone(consigneePhone);
				}
				
				order.setConsignee(consignee);
				order.setAddress(address);
				order.setUpdateTime(DateUtil.getCurrentDate());
				this.orderManager.save(order);
				
				resultMap.put("error", false);
				resultMap.put("success", true);
				resultMap.put("message", "保存成功");
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "错误, 保存失败");
		return resultMap;
	}
	
	
	/**
	 * 订单备注
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderRemarks")
	public @ResponseBody Map<String, Object> orderRemarks(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		String remarks = StringUtil.null2Str(request.getParameter("remarks"));

		
		try {
			if(StringUtil.isNull(remarks)) {
				resultMap.put("success", false);
				resultMap.put("error", true);
				resultMap.put("message", getText("备注不能为空"));
				return resultMap;
			}
			Order order = this.orderManager.get(orderId);
			if(order != null && order.getOrderId() != null ) {
				order.setRemarks(remarks);
				order.setUpdateTime(DateUtil.getCurrentDate());
				this.orderManager.save(order);
				
				resultMap.put("success", true);
				resultMap.put("error", false);
				resultMap.put("message", getText("save.success"));
				return resultMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("error", true);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	
	@RequestMapping(value = "/sentOrder")
	public @ResponseBody Map<String, Object> sentOrder(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = request.getParameter("idListGridJson");
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 检查订单是否有效
			List<Order> orderList = orderManager.getByIdList(idList);
			if (orderList == null || orderList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			boolean isExistError = false;
			StringBuffer errorBuffer = new StringBuffer();
			for (Order order : orderList) {
				if(!StringUtil.compareObject(order.getStatus(), OrderStatus.UN_DELIVER_ORDER_STATUS)) {
					isExistError = true;
					errorBuffer.append(String.format("<br>订单%s状态错误", order.getOrderNo()));
				}else if(!StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
					isExistError = true;
					errorBuffer.append(String.format("<br>订单%s未支付", order.getOrderNo()));
				}
			}

			if (isExistError) {
				resultMap.put("success", false);
				resultMap.put("message", String.format("订单发货错误%s", errorBuffer.toString()));
				return resultMap;
			}
			
			for (Order order : orderList) {
				order.setStatus(OrderStatus.DELIVER_ORDER_STATUS);
				order.setSentTime(DateUtil.getCurrentDate());
				order.setUpdateTime(DateUtil.getCurrentDate());
			}
			
			orderList = this.orderManager.batchInsert(orderList, orderList.size());
			
			try {
				for(Order order : orderList) {
					Order orderInfo = this.orderManager.getOrderByOrderId(StringUtil.nullToLong(order.getOrderId()));
					OrderItems orderItems = orderInfo.getOrderItemsList().get(0);
					UserInfo userInfo = userInfoManager.get(orderInfo.getUserId());
					if(userInfo != null 
							&& userInfo.getUserId() != null 
							&& userInfo.getOpenId() != null
							&& orderItems != null && orderItems.getItemId() != null) {
						WxSendUtil.sentSucc(order, userInfo, StringUtil.null2Str(orderItems.getProductName()));
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			resultMap.put("success", true);
			resultMap.put("message", getText("发货成功"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("发货失败"));
		return resultMap;
	}
	
	
}
