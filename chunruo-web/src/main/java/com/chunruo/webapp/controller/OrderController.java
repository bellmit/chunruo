package com.chunruo.webapp.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.Constants.PaymentType;
import com.chunruo.core.Constants.UnDeliverStatus;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.ExpressCode;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderHistory;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.OrderPackage;
import com.chunruo.core.model.OrderSync;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.OrderHistoryManager;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.OrderPackageManager;
import com.chunruo.core.service.OrderSyncManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.MyExcelExport;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.XlsUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.vo.TModel;
import com.chunruo.security.model.User;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.ImportFileController;
import com.chunruo.webapp.interceptor.AuthorizeInterceptor;
import com.chunruo.webapp.util.OrderUtil;
import com.chunruo.webapp.vo.AbnormalOrderVo;
import com.chunruo.webapp.vo.ExpressCodeVo;

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
	private OrderSyncManager orderSyncManager;
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

	/**
	 * 已完成列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderCompletedList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderCompletedList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("status", OrderStatus.OVER_ORDER_STATUS); // 已完成
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
	 * 已取消列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderCancelList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderCancelList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("status", OrderStatus.CANCEL_ORDER_STATUS); // 已取消
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
	 * 订单拦截
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderInterceptList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderInterceptList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("isIntercept", true); // 订单拦截状态
			paramMap.put("unDeliverStatus", 0);//代发货未发起取消订单

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
	 * 取消受理中订单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderAcceptanceList")
	@AuthorizeInterceptor(value="isExporter=true")
	public @ResponseBody Map<String, Object> orderAcceptanceList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("isPushErp", true); // 订单拦截状态
			paramMap.put("isIntercept", true); // 订单拦截状态
			paramMap.put("unDeliverStatus", UnDeliverStatus.UN_DELIVER_CANCELING);//待发货发起取消订单
			paramMap.put("status", OrderStatus.UN_DELIVER_ORDER_STATUS);  //待发货

			TModel<List<Order>> tmodel = this.getOrderDataList(paramMap, request);
			resultMap.put("data", tmodel.gettModel());
			resultMap.put("totalCount", tmodel.getCount());
			resultMap.put("filters", tmodel.getFiltersMap());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 订单列表工具类
	 * @param paramMap
	 * @param request
	 * @return
	 */
	public TModel<List<Order>> getOrderDataList(Map<String, Object> paramMap, final HttpServletRequest request) {
		int start = StringUtil.nullToInteger(request.getParameter("start"));
		int limit = StringUtil.nullToInteger(request.getParameter("limit"));
		String sort = StringUtil.nullToString(request.getParameter("sort"));
		String filters = StringUtil.nullToString(request.getParameter("filters"));
		Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
		Map<String, Object> filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Order.class);
		boolean isExporter = StringUtil.nullToBoolean(request.getParameter("isExporter"));
		boolean isSaleExporter = StringUtil.nullToBoolean(request.getParameter("isSaleExporter"));
		String columns = StringUtil.nullToString(request.getParameter("columns"));
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");

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

			// 是否订单导出
			if (isExporter) {
				Map<String, String> columnMap = StringUtil.getColumnsMap(columns);
				columnMap.put("orderNo", "订单号");

				if (columnMap != null && columnMap.size() > 0) {
					//销售导出数据
					if(isSaleExporter){
						Date dateBeginTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, beginTime);
						Date dateEndTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, endTime);
						orderList = orderManager.getOrderListByTime(dateBeginTime, dateEndTime, null);
						if (orderList != null && orderList.size() > 0) {
							// 导出列表头信息
							List<String> headerList = new ArrayList<String>();
							headerList.add("订单号");
							headerList.add("下单平台");
							headerList.add("创建时间");
							headerList.add("收货人");
							headerList.add("收货地址");
							headerList.add("收货人电话");
							headerList.add("支付方式");
							headerList.add("订单状态");
							headerList.add("订单金额");
							headerList.add("实际付款金额");
							headerList.add("店铺名称");
							headerList.add("店铺账号");
							headerList.add("用户身份");
							headerList.add("下单方式");
							headerList.add("是否占用名额");
							String paymentTypeName = "支付方式";
							String statusName = "订单状态";

							// 订单状态
							Map<Integer, String> orderStatusMap = new HashMap<Integer, String> ();
							orderStatusMap.put(1, "未支付");
							orderStatusMap.put(2, "未发货");
							orderStatusMap.put(3, "已发货");
							orderStatusMap.put(4, "已完成");
							orderStatusMap.put(5, "已取消");
							orderStatusMap.put(6, "退款中");

							// 用户身份
							Map<Integer, String> userInfoLevelMap = new HashMap<Integer, String> ();
							userInfoLevelMap.put(UserLevel.USER_LEVEL_COMMON, "普通用户");
							userInfoLevelMap.put(UserLevel.USER_LEVEL_BUYERS, "VIP");
							userInfoLevelMap.put(UserLevel.USER_LEVEL_DEALER, "经销商");
							userInfoLevelMap.put(UserLevel.USER_LEVEL_AGENT, "总代");
							userInfoLevelMap.put(UserLevel.USER_LEVEL_V2, "V2");
							userInfoLevelMap.put(UserLevel.USER_LEVEL_V3, "V3");
							
							List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
							orderList = OrderUtil.getStoreAndUserName(orderList, isIntercept);
							for (Order order : orderList) {
								try {
									Map<String, String> objectMap = new HashMap<String, String>();
									Map<String, Object> orderMap = StringUtil.objectToMap(order);
									for (Entry<String, String> entry : columnMap.entrySet()) {
										if (orderMap.containsKey(entry.getKey())) {
											objectMap.put(entry.getValue(), StringUtil.null2Str(orderMap.get(entry.getKey())));
											objectMap.put("下单平台", "纯若");
											objectMap.put("店铺账号", order.getStoreMobile());

											// 用户身份
											objectMap.put("用户身份", userInfoLevelMap.get(StringUtil.nullToInteger(order.getUserLevel())));

											// 下单方式
											objectMap.put("下单方式", "app下单");
											if (order.getWeChatConfigId() != null) {
												if (StringUtil.compareObject(order.getWeChatConfigId(), 5)) {
													objectMap.put("下单方式", "app下单");
												} else if (StringUtil.compareObject(order.getWeChatConfigId(), 1)) {
													objectMap.put("下单方式", "微店下单");
												}else if (StringUtil.compareObject(order.getWeChatConfigId(), 2)) {
													objectMap.put("下单方式", "小程序下单");
												}
											}
										}
									}

									// 订单支付方式
									if (objectMap.containsKey(paymentTypeName) && objectMap.get(paymentTypeName) != null) {
										Integer paymentType = StringUtil.nullToInteger(objectMap.get(paymentTypeName));
										if (StringUtil.compareObject(paymentType, PaymentType.PAYMENT_TYPE_WECHAT)) {
											objectMap.put(paymentTypeName, "微信支付");
										} else if (StringUtil.compareObject(paymentType, PaymentType.PAYMENT_TYPE_ALIPAY)) {
											objectMap.put(paymentTypeName, "支付宝支付");
										} else if (StringUtil.compareObject(paymentType, PaymentType.PAYMENT_TYPE_EASYPAY)) {
											objectMap.put(paymentTypeName, "易生支付");
										}
									}

									// 1:未支付;2:未发货;3:已发货;4:已完成;5:已取消;6:退款中)
									if (objectMap.containsKey(statusName) && objectMap.get(statusName) != null) {
										Integer status = StringUtil.nullToInteger(objectMap.get(statusName));
										objectMap.put(statusName, orderStatusMap.get(StringUtil.nullToInteger(status)));
									}
									objectMapList.add(objectMap);
								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
							}

							// 导出文件地址
							String filePath = StringUtil.getUniqueDateFilePath(OrderController.XLS_FILE_NAME);
							File file = new File(Constants.DEPOSITORY_PATH + filePath);
							FileUtil.createNewFile(file);
							XlsUtil.writeFile(headerList, objectMapList, file.getPath());
							xlsFilePath = filePath;
						}
					}else{
						Date dateBeginTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, beginTime);
						Date dateEndTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, endTime);
						orderList = orderManager.getOrderListByTime(dateBeginTime, dateEndTime, null);

						if (orderList != null && orderList.size() > 0) {
							// 导出列表头信息
							List<String> headerList = new ArrayList<String>();
							for (Entry<String, String> entry : columnMap.entrySet()) {
								headerList.add(entry.getValue());
							}

							List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
							orderList = OrderUtil.getStoreAndUserName(orderList, isIntercept);
							for (int i = 0; i < orderList.size(); i++) {
								try {
									Map<String, String> objectMap = new HashMap<String, String>();
									Map<String, Object> orderMap = StringUtil.objectToMap(orderList.get(i));
									for (Entry<String, String> entry : columnMap.entrySet()) {
										if (orderMap.containsKey(entry.getKey())) {
											objectMap.put(entry.getValue(), StringUtil.null2Str(orderMap.get(entry.getKey())));
										}
									}
									objectMapList.add(objectMap);
								} catch (Exception e) {
									e.printStackTrace();
									continue;
								}
							}

							// 导出文件地址
							String filePath = StringUtil.getUniqueDateFilePath(OrderController.XLS_FILE_NAME);
							File file = new File(Constants.DEPOSITORY_PATH + filePath);
							FileUtil.createNewFile(file);
							XlsUtil.writeFile(headerList, objectMapList, file.getPath());
							xlsFilePath = filePath;
						}
					}
				}
			} else {
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
				// 隐藏订单手机号码
//				if(!RbacAuthorityService.isExistRbacAuthority("/admin/orderMobile.msp")){
//					//检查当前entity是否为托管状态（Managed），若为托管态，需要转变为游离态，这样entity的数据发生改变时，就不会自动同步到数据库中
//					//游离态的entity需要调用merge方法转为托管态。
//					this.orderManager.detach(order);
//					// 隐藏关键信息
//					order.setConsigneePhone(StringUtil.mobileFormat(order.getConsigneePhone()));
//				}
				
				// 支付收款账号
				if (StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
					if (StringUtil.compareObject(PaymentType.PAYMENT_TYPE_WECHAT, order.getPaymentType())) {
						// 微信支付
						if (Constants.WECHAT_CONFIG_ID_MAP.containsKey(StringUtil.nullToLong(order.getWeChatConfigId()))) {
							WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(order.getWeChatConfigId()));
							order.setAcceptPayName(StringUtil.null2Str(weChatAppConfig.getAcceptPayName()));
						}
					} else if (StringUtil.compareObject(PaymentType.PAYMENT_TYPE_ALIPAY, order.getPaymentType())) {
						// 支付宝支付
						order.setAcceptPayName("支付宝V2.0");
					} else if (StringUtil.compareObject(PaymentType.PAYMENT_TYPE_EASYPAY, order.getPaymentType())) {
						// 易生支付
						order.setAcceptPayName("易生支付");
					} else if (StringUtil.compareObject(PaymentType.PAYMENT_TYPE_HUIFU, order.getPaymentType())) {
						// 上海汇付
						order.setAcceptPayName("上海汇付");
					}
				}

				// 订单归属店铺
				ProductWarehouse warehouse = Constants.PRODUCT_WAREHOUSE_MAP.get(StringUtil.nullToLong(order.getWareHouseId()));
				if (warehouse != null && warehouse.getWarehouseId() != null) {
					order.setWareHouseName(warehouse.getName());
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
						// 订单归属店铺
						ProductWarehouse tmpWarehouse = Constants.PRODUCT_WAREHOUSE_MAP.get(StringUtil.nullToLong(orderItems.getWareHouseId()));
						if (tmpWarehouse != null && tmpWarehouse.getWarehouseId() != null) {
							orderItems.setWareHouseName(tmpWarehouse.getName());
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
	 * 订单还原
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderRestore")
	public @ResponseBody Map<String, Object> orderRestore(final HttpServletRequest request) {
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

			this.orderManager.updateOrderRestore(idList, message, this.getUserId(request));
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
	 * 订单快递信息修改
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editOrderPackage")
	public @ResponseBody Map<String,Object> editOrderPackage(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		Long packageId = StringUtil.nullToLong(request.getParameter("packageId"));
		String expressCodeStr = StringUtil.null2Str(request.getParameter("expressCode"));
		String expressNo = StringUtil.null2Str(request.getParameter("expressNo"));
		try {
			if(StringUtil.compareObject(packageId, 0)) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,请选择一条快递信息");
				return resultMap;
			}else if(StringUtil.isNull(expressCodeStr)) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,快递编码不能为空");
				return resultMap;
			}else if(StringUtil.isNull(expressNo)) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,快递单号不能为空");
				return resultMap;
			}
			//检查物流信息是否存在
			OrderPackage orderPackage = this.orderPackageManager.get(packageId);
			if(orderPackage == null || orderPackage.getPackageId() == null ) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,物流信息不存在错误");
				return resultMap;
			}
			// 检查订单是否存在
			Order order = this.orderManager.get(orderId);
			if (order == null || order.getOrderId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,订单不存在错误");
				return resultMap;
			}
			
			// 规整已存在的快递信息
			List<OrderSync> orderSyncManagerList = this.orderSyncManager.getOrderSyncListByOrderNumber(order.getOrderNo());
			if (orderSyncManagerList != null && orderSyncManagerList.size() > 0) {
				for (OrderSync orderSync : orderSyncManagerList) {
					if(StringUtil.compareObject(orderSync.getExpressNumber(), expressNo)) {
						resultMap.put("success", false);
						resultMap.put("message", "错误,此快递编码已存在");
						return resultMap;
					}
				}
			}
						
			ExpressCode expressCode = Constants.EXPRESS_CODE_MAP.get(expressCodeStr.toLowerCase());
	        List<OrderSync> orderSyncList = this.orderSyncManager.getOrderSyncListByOrderNumber(order.getOrderNo());
	        if(orderSyncList != null && orderSyncList.size() > 0) {
	        	for(OrderSync orderSync : orderSyncList) {
	        		if(StringUtil.compareObject(orderSync.getExpressNumber(), orderPackage.getExpressNo())) {
	        			orderSync.setExpressNumber(expressNo);
	        			orderSync.setLogisticCode(expressCode.getCompanyCode());
	        			orderSync.setLogisticName(expressCode.getCompanyName());
	        			orderSync.setUpdateTime(DateUtil.getCurrentDate());
	        			
	        			orderPackage.setExpressCode(orderSync.getLogisticCode());
	        			orderPackage.setExpressNo(expressNo);
	        			orderPackage.setExpressCompany(orderSync.getLogisticName());
	        			orderPackage.setIsHandler(true);
	        			orderPackage.setUpdateTime(DateUtil.getCurrentDate());
	        			this.orderSyncManager.updateOrderSyncExpress(orderSync, orderPackage);
	        			resultMap.put("success", true);
						resultMap.put("message", "修改成功");
						return resultMap;
	        		}
	        	}
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
        
		resultMap.put("success", false);
		resultMap.put("message", "修改失败");
		return resultMap;
	}

	/**
	 * 订单出库
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderOutLibrary")
	public @ResponseBody Map<String, Object> orderOutLibrary(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		String strExpressMap = StringUtil.null2Str(request.getParameter("expressMap"));

		try {
			// 检查快递公司是否存在
			Map<String, String> expressMap = StringUtil.getColumnsMap(strExpressMap);
			if (expressMap == null || expressMap.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,快递信息不能为空");
				return resultMap;
			} else {
				// 检查快递单信息是否有效
				for (Entry<String, String> entry : expressMap.entrySet()) {
					if (StringUtil.isNull(entry.getKey())) {
						resultMap.put("success", false);
						resultMap.put("message", "错误,快递单号不能为空");
						return resultMap;
					} else if (StringUtil.isNull(entry.getValue())) {
						resultMap.put("success", false);
						resultMap.put("message", "错误,快递编码不能为空");
						return resultMap;
					} else if (!Constants.EXPRESS_CODE_MAP.containsKey(entry.getValue().toLowerCase())) {
						resultMap.put("success", false);
						resultMap.put("message", "错误,快递编码不存在");
						return resultMap;
					}
				}
			}

			// 检查订单是否存在
			Order order = this.orderManager.get(orderId);
			if (order == null || order.getOrderId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,订单不存在错误");
				return resultMap;
			}else if(StringUtil.nullToBoolean(order.getIsSplitSingle())) {
				resultMap.put("success", false);
				resultMap.put("message", "主订单不能设置出库");
				return resultMap;
			}

			// 检查订单状态是否符合仓库
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS); // 未发货
			statusList.add(OrderStatus.DELIVER_ORDER_STATUS); // 已发货
			if (!statusList.contains(StringUtil.nullToInteger(order.getStatus()))) {
				resultMap.put("success", false);
				resultMap.put("message", "错误,订单状态不支持订单出库操作");
				return resultMap;
			}

			// 规整已存在的快递信息
			Map<String, OrderSync> orderSyncMap = new HashMap<String, OrderSync>();
			List<OrderSync> orderSyncManagerList = this.orderSyncManager.getOrderSyncListByOrderNumber(order.getOrderNo());
			if (orderSyncManagerList != null && orderSyncManagerList.size() > 0) {
				for (OrderSync orderSync : orderSyncManagerList) {
					orderSyncMap.put(orderSync.getExpressNumber(), orderSync);
				}
			}

			// 新增和更新
			List<OrderSync> orderSyncList = new ArrayList<OrderSync>();
			for (Entry<String, String> entry : expressMap.entrySet()) {
				// 检查同步的快递信息是否已存在
				if (orderSyncMap.containsKey(entry.getKey())) {
					continue;
				}

				ExpressCode expressCode = Constants.EXPRESS_CODE_MAP.get(entry.getValue().toLowerCase());
				OrderSync orderSync = new OrderSync();
				orderSync.setSyncNumber(0);
				orderSync.setIsSyncSucc(false);
				orderSync.setIsHandler(true);
				orderSync.setOrderStatus(OrderSync.ORDER_SYNC_STATUS_SUCCESS);
				orderSync.setOrderNumber(order.getOrderNo());
				orderSync.setExpressNumber(entry.getKey());
				orderSync.setLogisticCode(expressCode.getCompanyCode());
				orderSync.setLogisticName(expressCode.getCompanyName());
				orderSync.setCreateTime(DateUtil.getCurrentDate());
				orderSync.setUpdateTime(orderSync.getCreateTime());
				orderSyncList.add(orderSync);
			}
			this.orderSyncManager.saveOrderSyncOutLibrary(orderSyncList, this.getUserId(request));

			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	/**
	 * 手动订单商品修正
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderHanderProductCorrection")
	public @ResponseBody Map<String, Object> orderHanderProductCorrection(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));

		try {
			List<Long> idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			List<Order> orderList = this.orderManager.getByIdList(idList);
			if (orderList == null || orderList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 检查直邮订单是否符合转让待出库状态
			boolean isExistError = false;
			StringBuffer errorBuffer = new StringBuffer();
			for (Order order : orderList) {
				if (!StringUtil.compareObject(order.getStatus(), OrderStatus.UN_DELIVER_ORDER_STATUS)) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s订单状态不支持", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsPushErp())) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s订单状态不支持", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s已拆单订单不支持", order.getOrderNo()));
				}
			}
			
			// 订单处理错误信息
			if (isExistError) {
				resultMap.put("success", false);
				resultMap.put("message", getText("订单处理错误信息", errorBuffer.toString()));
				return resultMap;
			}
			
			// 自动拆单和修改订单仓库信息
			for(final Order order : orderList){
				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							OrderController.autoOrderProductInfoCorrection(order.getOrderId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}
			
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}

	/**
	 * 直邮等待出库
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/directMailOrderWaitLibrary")
	public @ResponseBody Map<String, Object> directMailOrderWaitLibrary(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));

		try {
			List<Long> idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 直邮仓库
			List<Long> wareHouseIdList = new ArrayList<Long>();
			if (Constants.HANDER_WAREHOUSE_MAP != null && Constants.HANDER_WAREHOUSE_MAP.size() > 0) {
				for (ProductWarehouse productWarehouse : Constants.HANDER_WAREHOUSE_MAP.values()) {
					if (!StringUtil.nullToBoolean(productWarehouse.getIsDirectPushErp())) {
						wareHouseIdList.add(productWarehouse.getWarehouseId());
					}
				}
			}

			// 直邮仓库没有配置
			if (wareHouseIdList == null || wareHouseIdList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			List<Order> orderList = this.orderManager.getByIdList(idList);
			if (orderList == null || orderList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 检查直邮订单是否符合转让待出库状态
			boolean isExistError = false;
			StringBuffer errorBuffer = new StringBuffer();
			for (Order order : orderList) {
				if (!StringUtil.compareObject(order.getStatus(), OrderStatus.UN_DELIVER_ORDER_STATUS)) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s订单状态不支持", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsPushErp())) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s订单状态不支持", order.getOrderNo()));
				} else if (!wareHouseIdList.contains(StringUtil.nullToLong(order.getWareHouseId()))) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s非直邮订单", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsSplitSingle())) {
					isExistError = true;
					errorBuffer.append(String.format("<br/>%s主订单不支持", order.getOrderNo()));
				}
			}

			// 直邮订单处理错误信息
			if (isExistError) {
				resultMap.put("success", false);
				resultMap.put("message", getText("order.directMail.wait.library.error", errorBuffer.toString()));
				return resultMap;
			}

			this.orderManager.updateDirectMailWaitLibraryStatus(idList, this.getUserId(request));
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}

	/**
	 * 手动导入快速信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/expressNumberImport")
	public @ResponseBody Map<String, Object> expressNumberImport(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String headerGridJson = StringUtil.null2Str(request.getParameter("headerGridJson"));
		String dataGridJson = StringUtil.null2Str(request.getParameter("dataGridJson"));
		List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
		try {
			List<Object> headerListMap = StringUtil.jsonDeserialize(headerGridJson);
			if (headerListMap == null || headerListMap.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}

			List<Object> dataListMap = StringUtil.jsonDeserialize(dataGridJson);
			if (dataListMap == null || dataListMap.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}

			objectMapList = ImportFileController.importDataToMapList(dataListMap, headerListMap);
			if (objectMapList == null || objectMapList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("errors.select.object"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}

		try {
			int index = 0;
			boolean isExitError = false;
			StringBuffer errorBuffer = new StringBuffer();
			Set<String> orderNoSet = new HashSet<String>();
			List<ExpressCodeVo> expressNumberList = new ArrayList<ExpressCodeVo>();
			for (Map<String, String> objectMap : objectMapList) {
				String orderNumber = StringUtil.null2Str(objectMap.get("订单号"));
				String expressNumber = StringUtil.null2Str(objectMap.get("快递单号"));
				String expressCode = StringUtil.null2Str(objectMap.get("快递编码"));

				++index;
				if (!StringUtil.isNull(orderNumber) && !StringUtil.isNull(expressCode)
						&& !StringUtil.isNull(expressNumber)) {
					orderNoSet.add(orderNumber);

					// 检查对应的快递信息是否存在
					ExpressCode objectExpressCode = Constants.EXPRESS_CODE_MAP.get(expressCode.toLowerCase());
					if (objectExpressCode == null || objectExpressCode.getCodeId() == null) {
						isExitError = true;
						errorBuffer.append(String.format("<br/>第%s行,快递编码错误", index));
					} else {
						ExpressCodeVo expressCodeVo = new ExpressCodeVo();
						expressCodeVo.setOrderNumber(orderNumber);
						expressCodeVo.setExpressNumber(expressNumber);
						expressCodeVo.setExpressCode(objectExpressCode.getCompanyCode());
						expressCodeVo.setCompanyName(objectExpressCode.getCompanyName());
						expressNumberList.add(expressCodeVo);
					}
				} else {
					isExitError = true;
					if (StringUtil.isNull(orderNumber)) {
						errorBuffer.append(String.format("<br/>第%s行,订单号不存在或订单号为空", index));
					} else if (StringUtil.isNull(expressNumber)) {
						errorBuffer.append(String.format("<br/>第%s行,快递单号不存在或快递单号为空", index));
					} else if (StringUtil.isNull(expressCode)) {
						errorBuffer.append(String.format("<br/>第%s行,快递编码不存在或快递编码为空", index));
					}
				}
			}

			// 是否存在错误
			if (isExitError) {
				resultMap.put("success", false);
				resultMap.put("message", this.getText("order.import.file.error", errorBuffer.toString()));
				return resultMap;
			}

			List<Order> orderList = this.orderManager.getOrderListByOrderNoList(StringUtil.strSetToList(orderNoSet));
			if (orderList == null || orderList.size() <= 0) {
				// 订单号全部无效
				resultMap.put("success", false);
				resultMap.put("message", this.getText("order.import.file.error", errorBuffer.toString()));
				return resultMap;
			} else if (!StringUtil.compareObject(orderList.size(), orderNoSet.size())) {
				Map<String, Order> orderMap = new HashMap<String, Order>();
				for (Order order : orderList) {
					orderMap.put(order.getOrderNo(), order);
				}

				boolean isNoExitError = false;
				StringBuffer noExitErrorBuffer = new StringBuffer();
				for (String orderNumber : orderNoSet) {
					if (!orderMap.containsKey(orderNumber)) {
						isNoExitError = true;
						noExitErrorBuffer.append(String.format("<br/>订单号%s记录不存在", orderNumber));
					}
				}

				// 订单号数据库不存在
				if (isNoExitError) {
					resultMap.put("success", false);
					resultMap.put("message", this.getText("order.import.file.error", noExitErrorBuffer.toString()));
					return resultMap;
				}
			} else {
				Map<String, Order> orderMap = new HashMap<String, Order>();
				for (Order order : orderList) {
					orderMap.put(order.getOrderNo(), order);
				}

				boolean isNoExitError = false;
				StringBuffer noExitErrorBuffer = new StringBuffer();
				for (ExpressCodeVo expressCodeVo : expressNumberList) {
					if (!orderMap.containsKey(expressCodeVo.getOrderNumber())) {
						isNoExitError = true;
						noExitErrorBuffer.append(String.format("<br/>订单号%s记录不存在", expressCodeVo.getOrderNumber()));
					} else {
						Order order = orderMap.get(expressCodeVo.getOrderNumber());
						if (!StringUtil.compareObject(order.getStatus(), OrderStatus.UN_DELIVER_ORDER_STATUS)
								|| !StringUtil.nullToBoolean(order.getIsPushErp())) {
							noExitErrorBuffer
									.append(String.format("<br/>订单号%s状态不支持导入快递信息", expressCodeVo.getOrderNumber()));
						}
					}
				}

				// 订单号数据库不存在
				if (isNoExitError) {
					resultMap.put("success", false);
					resultMap.put("message", this.getText("order.import.file.error", noExitErrorBuffer.toString()));
					return resultMap;
				}
			}

			// 规整已存在的快递信息
			Map<String, List<OrderSync>> orderSyncListMap = new HashMap<String, List<OrderSync>>();
			List<OrderSync> orderSyncManagerList = this.orderSyncManager
					.getOrderSyncListByOrderNumberList(StringUtil.strSetToList(orderNoSet));
			if (orderSyncManagerList != null && orderSyncManagerList.size() > 0) {
				for (OrderSync orderSync : orderSyncManagerList) {
					if (!orderSyncListMap.containsKey(orderSync.getOrderNumber())) {
						List<OrderSync> orderSyncList = new ArrayList<OrderSync>();
						orderSyncList.add(orderSync);
						orderSyncListMap.put(orderSync.getOrderNumber(), orderSyncList);
					} else {
						orderSyncListMap.get(orderSync.getOrderNumber()).add(orderSync);
					}
				}
			}

			// 新增和更新
			List<OrderSync> orderSyncList = new ArrayList<OrderSync>();
			for (ExpressCodeVo expressCodeVo : expressNumberList) {
				// 检查同步的快递信息是否已存在
				if (orderSyncListMap.containsKey(expressCodeVo.getOrderNumber())) {
					List<OrderSync> syncList = orderSyncListMap.get(expressCodeVo.getOrderNumber());
					if (syncList != null && syncList.size() > 0) {
						boolean isExist = false;
						for (OrderSync orderSync : syncList) {
							if (StringUtil.compareObject(orderSync.getExpressNumber(),
									expressCodeVo.getOrderNumber())) {
								isExist = true;
								break;
							}
						}

						// 快递信息已存在直接退出
						if (isExist) {
							continue;
						}
					}
				}

				OrderSync orderSync = new OrderSync();
				orderSync.setSyncNumber(0);
				orderSync.setIsSyncSucc(false);
				orderSync.setIsHandler(true);
				orderSync.setOrderStatus(OrderSync.ORDER_SYNC_STATUS_SUCCESS);
				orderSync.setOrderNumber(expressCodeVo.getOrderNumber());
				orderSync.setExpressNumber(expressCodeVo.getExpressNumber());
				orderSync.setLogisticCode(expressCodeVo.getExpressCode());
				orderSync.setLogisticName(expressCodeVo.getCompanyName());
				orderSync.setCreateTime(DateUtil.getCurrentDate());
				orderSync.setUpdateTime(orderSync.getCreateTime());
				orderSyncList.add(orderSync);
			}
			this.orderSyncManager.saveOrderSyncOutLibrary(orderSyncList, this.getUserId(request));

			resultMap.put("success", true);
			resultMap.put("message", this.getText("ajax.import.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", this.getText("ajax.import.failure"));
		return resultMap;
	}

	/**
	 * 直邮导出
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderDirectMailExport")
	public @ResponseBody Map<String, Object> orderDirectMailExport(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long warehouseId = StringUtil.nullToLong(request.getParameter("warehouseId"));
		try {
			// 直邮仓库
			ProductWarehouse productWarehouse = Constants.HANDER_WAREHOUSE_MAP.get(warehouseId);
			if (productWarehouse == null || productWarehouse.getWarehouseId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 查询是否存在直邮订单
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("status", OrderStatus.UN_DELIVER_ORDER_STATUS);
			paramMap.put("isSplitSingle", false);
			paramMap.put("isPushErp", false);
			paramMap.put("isDirectPushErp", false);
			paramMap.put("wareHouseId", productWarehouse.getWarehouseId());
			List<Order> orderList = this.orderManager.getHqlPages(paramMap);
			if (orderList == null || orderList.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			// 导出列表头信息
			List<String> headerList = new ArrayList<String>();
			headerList.add("序号");
			headerList.add("运单号");
			headerList.add("参考号");
			headerList.add("发货人");
			headerList.add("发货人电话");
			headerList.add("收货人");
			headerList.add("电话");
			headerList.add("收货人证件号");
			headerList.add("身份证正面");
			headerList.add("身份证反面");
			headerList.add("地址");
			headerList.add("区");
			headerList.add("市");
			headerList.add("洲/省");
			headerList.add("邮编");
			headerList.add("毛重(kg)");
			headerList.add("体积(m3)");
			headerList.add("产品条码");
			headerList.add("中文品名");
			headerList.add("件数");
			headerList.add("主规格");
			headerList.add("次规格");
			headerList.add("HS编码");
			headerList.add("保费");
			headerList.add("备注");

			List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
			for (int i = 0; i < orderList.size(); i++) {
				try {
					// 订单产品列表
					Order order = orderList.get(i);
					List<OrderItems> orderItemsList = new ArrayList<OrderItems>();
					if (StringUtil.nullToBoolean(order.getIsSubOrder())) {
						orderItemsList = this.orderItemsManager.getOrderSubItemsListByOrderId(order.getParentOrderId(),
								order.getOrderId());
					} else {
						orderItemsList = this.orderItemsManager.getOrderItemsListByOrderId(order.getOrderId());
					}

					if (orderItemsList != null && orderItemsList.size() > 0) {
						for (OrderItems orderItems : orderItemsList) {
							Map<String, String> objectMap = new HashMap<String, String>();
							objectMap.put("序号", StringUtil.null2Str(i + 1));
							objectMap.put("运单号", "");
							objectMap.put("参考号", StringUtil.null2Str(order.getOrderNo()));
							objectMap.put("发货人", "澳洲PCA仓库");
							objectMap.put("发货人电话", "18156590620");
							objectMap.put("收货人", StringUtil.null2Str(order.getConsignee()));
							objectMap.put("电话", StringUtil.null2Str(order.getConsigneePhone()));
							objectMap.put("收货人证件号", StringUtil.null2Str(order.getIdentityNo()));
							objectMap.put("身份证正面", StringUtil.null2Str(order.getIdentityFront()));
							objectMap.put("身份证反面", StringUtil.null2Str(order.getIdentityBack()));
							objectMap.put("地址", OrderController.getFullAddressInfo(order.getProvinceId(),
									order.getCityId(), order.getAreaId(), order.getAddress()));
							objectMap.put("区", OrderController.getAreaNameById(order.getAreaId()));
							objectMap.put("市", OrderController.getAreaNameById(order.getCityId()));
							objectMap.put("洲/省", OrderController.getAreaNameById(order.getProvinceId()));
							objectMap.put("邮编", "");
							objectMap.put("毛重(kg)", "0");
							objectMap.put("体积(m3)", "");

							// 商品详情
							objectMap.put("产品条码", StringUtil.null2Str(orderItems.getProductCode()));
							objectMap.put("中文品名", StringUtil.null2Str(orderItems.getProductName()));
							objectMap.put("件数", StringUtil.null2Str(orderItems.getQuantity()));
							objectMap.put("主规格", String.format("%s%s",StringUtil.null2Str(orderItems.getPrimarySpecName()),StringUtil.null2Str(orderItems.getPrimarySpecModelName())));
							objectMap.put("次规格", String.format("%s%s",StringUtil.null2Str(orderItems.getSecondarySpecName()),StringUtil.null2Str(orderItems.getSecondarySpecModelName())));
							objectMap.put("HS编码", StringUtil.null2Str(orderItems.getProductSku()));

							objectMap.put("保费", "");
							objectMap.put("备注", "");
							objectMapList.add(objectMap);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String filePath = StringUtil.getUniqueDateFilePath(OrderController.XLS_FILE_NAME);
			File file = new File(Constants.DEPOSITORY_PATH + filePath);
			FileUtil.createNewFile(file);
			XlsUtil.writeFile(headerList, objectMapList, file.getPath());

			resultMap.put("success", true);
			resultMap.put("filePath", filePath);
			resultMap.put("message", getText("ajax.import.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("errors.nuKnow"));
		return resultMap;
	}

	/**
	 * 手动导出订单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/downLoadExportFile")
	public @ResponseBody void downLoadExportFile(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			String filePath = StringUtil.null2Str(request.getParameter("filePath"));
			if (FileUtil.checkFileExists(Constants.DEPOSITORY_PATH + filePath)) {
				File file = new File(Constants.DEPOSITORY_PATH + filePath);
				// 以流的形式下载文件。
				InputStream fis = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();

				String filename = "纯若_" + DateUtil.formatDate("yyyy年MM月dd日", DateUtil.getCurrentDate())
						+ OrderController.XLS_FILE_NAME;
				// 清空response
				response.reset();
				// 设置response的Header

				if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
					filename = URLEncoder.encode(filename, "UTF-8");
				} else {
					filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
				}

				response.addHeader("Content-Disposition", "attachment;filename=" + filename);
				response.addHeader("Content-Length", StringUtil.null2Str(file.length()));
				OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/vnd.ms-excel;charset=gb2312");
				toClient.write(buffer);
				toClient.flush();
				toClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@RequestMapping(value = "/exportOrderExcel")
	public @ResponseBody void exportExcel(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		String beginTime = request.getParameter("beginTime");
		String endTime = request.getParameter("endTime");
		String phone = request.getParameter("phone");
		String status = request.getParameter("status");
		
		try {
			List<Object[]> dataList = OrderUtil.orderReport(beginTime, endTime, phone, status, null);
			MyExcelExport myExcel = new MyExcelExport("OrderReport", Constants.ORDER_REPORT_COLUMN_NAME, dataList, response);
			myExcel.export();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 更改订单买家信息
	@RequestMapping(value = "/editBuyertInfo")
	public @ResponseBody Map<String, Object> editBuyerInfo(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		String identityName = StringUtil.nullToString(request.getParameter("identityName"));
		String identityNo = StringUtil.nullToString(request.getParameter("identityNo"));
		String consigneePhone = StringUtil.nullToString(request.getParameter("consigneePhone"));
		String consignee = StringUtil.nullToString(request.getParameter("consignee"));
		String address = StringUtil.nullToString(request.getParameter("address"));
		if (StringUtil.isNull(orderId)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("ajax.no.record"));
			return resultMap;
		}else if (!StringUtil.isValidIdentityCardNO(identityNo)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("错误,请正确输入身份证号码"));
			return resultMap;
		} else if (StringUtil.isNull(address)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("错误,请正确输入收货地址"));
			return resultMap;
		}else if (StringUtil.isNull(identityName) || StringUtil.isOneChineseCharacters(identityName)) {
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("错误,请正确输入身份证真实姓名"));
			return resultMap;
		}else if (StringUtil.isNull(consignee) || StringUtil.isOneChineseCharacters(identityName)) {
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
				
				order.setIdentityNo(identityNo);
				order.setIdentityName(identityName);
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
	 * 所有异常订单
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/abnormalOrderlist")
	public @ResponseBody Map<String, Object> abnormalOrderlist(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();

		Long count = 0L;
		List<Order> orderList = new ArrayList<Order>();
		List<AbnormalOrderVo> abnormalOrderVoList = new ArrayList<AbnormalOrderVo>();
		List<AbnormalOrderVo> subAbnormalOrderVoList = new ArrayList<AbnormalOrderVo>();
		List<Order> abnormalList = null;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Order.class);

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.orderManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				orderList = this.orderManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));
			}

			abnormalList = this.orderManager.getAbmormalOrderList();
			if (filtersMap != null && filtersMap.size() > 0) {
				//过滤
				@SuppressWarnings("unchecked")
				List<Order> interList = (List<Order>) CollectionUtils.intersection(orderList, abnormalList);
				abnormalList.clear();
				if (!CollectionUtils.isEmpty(interList)) {
					abnormalList.addAll(interList);
				}
			}
			if (!CollectionUtils.isEmpty(abnormalList)) {
				for (Order order : abnormalList) {
					AbnormalOrderVo abnormalOrderVo = new AbnormalOrderVo();
					abnormalOrderVo.setOrderId(order.getOrderId());
					abnormalOrderVo.setOrderNo(order.getOrderNo());
					abnormalOrderVo.setPayTime(order.getPayTime());
					abnormalOrderVo.setNotdeliverDays((DateUtil.getCurrentTime() - order.getPayTime().getTime()) / (24 * 60 * 60 * 1000));
					abnormalOrderVoList.add(abnormalOrderVo);
				}
			}
			if (start + limit > abnormalOrderVoList.size()) {
				subAbnormalOrderVoList.addAll(abnormalOrderVoList.subList(start, abnormalOrderVoList.size()));
			} else {
				subAbnormalOrderVoList.addAll(abnormalOrderVoList.subList(start, start + limit));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", subAbnormalOrderVoList);
		resultMap.put("totalCount", abnormalList.size());
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	/**
	 * 异常订单导出
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/exportAbnormalOrderExcel")
	public @ResponseBody void exportAbnormalOrderExcel(final HttpServletRequest request, final HttpServletResponse response)
			throws UnsupportedEncodingException {
		String status = request.getParameter("status");
		try {
			List<Object[]> dataList = OrderUtil.orderReport(null, null, null, status, null);
			MyExcelExport myExcel = new MyExcelExport("OrderReport", Constants.ORDER_REPORT_COLUMN_NAME, dataList, response);
			myExcel.export();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 订单商品信息修正
	 * @param orderId
	 */
	private static void autoOrderProductInfoCorrection(Long orderId){
		OrderManager orderManager = Constants.ctx.getBean(OrderManager.class);
		ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
		OrderItemsManager orderItemsManager = Constants.ctx.getBean(OrderItemsManager.class);
		ProductSpecManager productSpecManager = Constants.ctx.getBean(ProductSpecManager.class);
		
		Order order = orderManager.get(orderId);
		if(order == null 
				|| order.getOrderId() == null
				|| StringUtil.nullToBoolean(order.getIsSplitSingle())
				|| !StringUtil.compareObject(order.getStatus(), OrderStatus.UN_DELIVER_ORDER_STATUS)){
			return;
		}
		
		List<OrderItems> orderItemList = new ArrayList<OrderItems> ();
		if(StringUtil.nullToBoolean(order.getIsSubOrder())){
			// 被拆单的订单
			orderItemList = orderItemsManager.getOrderSubItemsListByOrderId(order.getParentOrderId(), order.getOrderId());
		}else{
			orderItemList = orderItemsManager.getOrderItemsListByOrderId(order.getOrderId());
		}

		// 检查商品列表信息是否有效
		if(orderItemList == null || orderItemList.size() <= 0){
			return;
		}

		// 根据订单商品列表找出正确仓库信息
		Set<Long> productIdSet = new HashSet<Long> ();
		Map<Long, OrderItems> orderItemsByProductIdMap = new HashMap<Long, OrderItems> ();
		Map<Long, OrderItems> orderItemsByProductSpecIdMap = new HashMap<Long, OrderItems> ();
		for(OrderItems orderItems : orderItemList){
			productIdSet.add(orderItems.getProductId());
			if(StringUtil.nullToBoolean(orderItems.getIsSpceProduct())){
				orderItemsByProductSpecIdMap.put(orderItems.getProductSpecId(), orderItems);
			}else{
				orderItemsByProductIdMap.put(orderItems.getProductId(), orderItems);
			}
		}

		// 查找所有的商品列表
		Map<Long, Product> productMap = new HashMap<Long, Product> ();
		List<Product> productList = productManager.getByIdList(StringUtil.longSetToList(productIdSet));
		if(productList != null && productList.size() > 0){
			for(Product product : productList){
				productMap.put(product.getProductId(), product);
			}
		}
		
		// 检查商品列表信息是否有效
		if(productMap == null || productMap.size() <= 0){
			return;
		}
		
		// 查找所有的规格商品列表
		Map<Long, ProductSpec> productSpecMap = new HashMap<Long, ProductSpec> ();
		if(orderItemsByProductSpecIdMap != null && orderItemsByProductSpecIdMap.size() > 0){
			List<ProductSpec> productSpecList = productSpecManager.getByIdList(StringUtil.longSetToList(orderItemsByProductSpecIdMap.keySet()));
			if(productSpecList != null && productSpecList.size() > 0){
				for(ProductSpec productSpec : productSpecList){
					productSpecMap.put(productSpec.getProductSpecId(), productSpec);
				}
			}
		}
		
		// 根据订单商品修复正确的信息
		if(orderItemsByProductIdMap != null && orderItemsByProductIdMap.size() > 0){
			// 普通商品
			for(Entry<Long, OrderItems> entry : orderItemsByProductIdMap.entrySet()){
				OrderItems item = entry.getValue();
				Product product = productMap.get(item.getProductId());
				if(product != null 
						&& !StringUtil.nullToBoolean(product.getIsSpceProduct())
						&& orderItemsByProductIdMap.containsKey(item.getProductId())){
					OrderItems orderItems = orderItemsByProductIdMap.get(item.getProductId());
					orderItems.setProductName(product.getName());
					orderItems.setProductCode(product.getProductCode());
					orderItems.setProductSku(product.getProductSku());
					orderItems.setUpdateTime(DateUtil.getCurrentDate());
				}
			}
		}
		
		// 规格商品
		if(orderItemsByProductSpecIdMap != null && orderItemsByProductSpecIdMap.size() > 0){
			for(Entry<Long, OrderItems> entry : orderItemsByProductSpecIdMap.entrySet()){
				OrderItems item = entry.getValue();
				Product product = productMap.get(item.getProductId());
				ProductSpec productSpec = productSpecMap.get(item.getProductSpecId());
				if(product != null 
						&& productSpec != null
						&& StringUtil.nullToBoolean(product.getIsSpceProduct())
						&& orderItemsByProductSpecIdMap.containsKey(item.getProductSpecId())){
					OrderItems orderItems = orderItemsByProductSpecIdMap.get(item.getProductSpecId());
					orderItems.setProductName(product.getName());
					orderItems.setProductCode(productSpec.getProductCode());
					orderItems.setProductSku(productSpec.getProductSku());
					orderItems.setUpdateTime(DateUtil.getCurrentDate());
				}
			}
		}
		
		order.setSyncNumber(0);
		order.setErrorMsg("");
		order.setUpdateTime(DateUtil.getCurrentDate());
		orderManager.update(order);
		orderItemsManager.batchInsert(orderItemList, orderItemList.size());
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
	
	
	/**
	 * 订单结算
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/orderSettlement")
	public @ResponseBody Map<String, Object> orderSettlement(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));

		
		try {
			Boolean isTest = StringUtil.nullToBoolean(Constants.conf.getProperty("SMS_TEST_MODEL"));
			if(!StringUtil.nullToBoolean(isTest)) {
				resultMap.put("success", false);
				resultMap.put("error", true);
				resultMap.put("message", getText("非测试环境不能执行此操作"));
				return resultMap;
			}
			Order order = this.orderManager.get(orderId);
			if(order != null && order.getOrderId() != null) {
				if(!StringUtil.nullToBoolean(order.getIsPaymentSucc())) {
					resultMap.put("success", false);
					resultMap.put("error", true);
					resultMap.put("message", getText("该订单未支付"));
					return resultMap;	
				}
				
				log.info("订单状态1:"+order.getStatus());
				orderManager.updateOrderCompleteStatus(OrderStatus.OVER_ORDER_STATUS, orderId);
				log.info("订单状态2:"+order.getStatus());
				order.setStatus(OrderStatus.OVER_ORDER_STATUS);
				this.orderManager.save(order);
				UserProfitRecordManager userProfitRecordManager = Constants.ctx.getBean(UserProfitRecordManager.class);
				userProfitRecordManager.orderCheckUpdateRecord(StringUtil.nullToLong(order.getOrderId()));
				
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
	
	
	
	
	/**
	 * 订单拦截
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/dealWithIntercept")
	public @ResponseBody Map<String, Object> dealWithIntercept(final HttpServletRequest request) {
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
				// 订单在未结算之前都可以拦截
				List<Integer> statusList = new ArrayList<Integer> ();
				statusList.add(OrderStatus.UN_DELIVER_ORDER_STATUS);
				statusList.add(OrderStatus.DELIVER_ORDER_STATUS);
				if (!statusList.contains(order.getStatus())) {
					// 检查订单状态是否有效
					isExistError = true;
					errorBuffer.append(String.format("<br>订单%s状态错误", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsCheck())) {
					// 检查订单是否已结算
					isExistError = true;
					errorBuffer.append(String.format("<br>订单%s已结算", order.getOrderNo()));
				} else if (StringUtil.nullToBoolean(order.getIsIntercept())) {
					// 检查订单是否已拦截
					isExistError = true;
					errorBuffer.append(String.format("<br>订单%s已拦截", order.getOrderNo()));
				}
			}

			// 订单推送ERP状态错误
			if (isExistError) {
				resultMap.put("success", false);
				resultMap.put("message", String.format("订单拦截错误%s", errorBuffer.toString()));
				return resultMap;
			}
			
			// 设置拦截状态
			for (Order order : orderList) {
				order.setIsIntercept(true);
				order.setRemarks("手动订单拦截");
				order.setUpdateTime(DateUtil.getCurrentDate());
			}
			this.orderManager.batchInsert(orderList, orderList.size());
			
			resultMap.put("success", true);
			resultMap.put("message", getText("order.push.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("order.push.failure"));
		return resultMap;
	}
	
	
	/**
	 * 手动导出身份证
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createIdCardZip")
	public @ResponseBody Map<String, Object>  createIdCardZip(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));
		String columns = StringUtil.nullToString(request.getParameter("columns"));
		try {
			User currentUser = this.getCurrentUser(request);
			if(currentUser == null) {
				resultMap.put("success", false);
				resultMap.put("message", getText("用户识别错误"));
				return resultMap;
			}
			
			List<Long> orderIdList = (List<Long>) StringUtil.getIdLongList(record);
			if(orderIdList != null && !orderIdList.isEmpty()) {
				Map<String, List<String>> filePathLitMap = new HashMap<String, List<String>> ();
				List<Order> orderList = this.orderManager.getByIdList(orderIdList);
				if(orderList != null && !orderList.isEmpty()) {
					for(Order order : orderList) {
						List<String> realFilePathList = new ArrayList<String> ();
						MsgModel<String> frontModel = checkFileIsExit(StringUtil.null2Str(order.getIdentityFront()));
						if(!StringUtil.nullToBoolean(frontModel.getIsSucc())) {
							continue;
						}
						MsgModel<String> backModel = checkFileIsExit(StringUtil.null2Str(order.getIdentityBack()));
						if(!StringUtil.nullToBoolean(backModel.getIsSucc())) {
							continue;
						}
						realFilePathList.add(frontModel.getData());
						realFilePathList.add(backModel.getData());
						filePathLitMap.put(StringUtil.null2Str(order.getOrderNo()), realFilePathList);
					}
					
					Map<String, String> columnMap = StringUtil.getColumnsMap(columns);
					columnMap.put("orderNo", "订单号");
					// 导出列表头信息
					List<String> headerList = new ArrayList<String>();
					for (Entry<String, String> entry : columnMap.entrySet()) {
						headerList.add(entry.getValue());
					}

					List<Map<String, String>> objectMapList = new ArrayList<Map<String, String>>();
					orderList = OrderUtil.getStoreAndUserName(orderList, false);
					for (int i = 0; i < orderList.size(); i++) {
						try {
							Map<String, String> objectMap = new HashMap<String, String>();
							Map<String, Object> orderMap = StringUtil.objectToMap(orderList.get(i));
							for (Entry<String, String> entry : columnMap.entrySet()) {
								if (orderMap.containsKey(entry.getKey())) {
									objectMap.put(entry.getValue(), StringUtil.null2Str(orderMap.get(entry.getKey())));
								}
							}
							objectMapList.add(objectMap);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}

					// 导出文件地址
					String filePath = StringUtil.getUniqueDateFilePath(OrderController.XLS_FILE_NAME);
					File file = new File(Constants.DEPOSITORY_PATH + filePath);
					FileUtil.createNewFile(file);
					XlsUtil.writeFile(headerList, objectMapList, file.getPath());
//					xlsFilePath = filePath;
				
					String zipPath = Constants.DEPOSITORY_PATH + StringUtil.getUniqueDateFilePath(OrderController.ZIP_FILE_NAME); // 压缩文件保存本地地址
					addZipFile(zipPath, file.getPath(), filePathLitMap,request,response);
					resultMap.put("filePath", zipPath);
					resultMap.put("success", true);
					return resultMap;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("下载失败"));
		return resultMap;
	}
	
	
	private MsgModel<String> checkFileIsExit(String filePath){
		MsgModel<String> msgModel = new MsgModel<String>();
		try {
			String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + StringUtil.null2Str(filePath);
//			String fullFilePath = "/Users/chunruo/Desktop/abcd/1.jpg";
			File file = new File(fullFilePath);
			if(!file.exists()) {
				//订单身份证信息不存在错误orderNumber
				msgModel.setIsSucc(false);
				msgModel.setMessage("身份证图片未找到");
				return msgModel;
			}
			msgModel.setIsSucc(true);
			msgModel.setData(StringUtil.null2Str(file.getPath()));
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("身份证图片错误");
		return msgModel;
	}
	
	
	public static void addZipFile(String filePath, String excelPath, Map<String, List<String>> filePathLitMap,HttpServletRequest request,HttpServletResponse response){
		ZipOutputStream out = null;
		try {
			System.out.println("filePaht==========="+filePath);
			if(filePathLitMap == null || filePathLitMap.size() <= 0) {
				return;
			}

			out = new ZipOutputStream(new FileOutputStream(filePath));
			out.setEncoding("GBK");
			
			// Excel订单列表
			File excelFile = new File(excelPath);
			if(excelFile.exists()) {
				FileInputStream fis = null;
				ByteArrayOutputStream bos = null;
				try {
					bos = new ByteArrayOutputStream();
					fis = new FileInputStream(excelFile);
					byte[] b = new byte[1024];
					int n;
					while ((n = fis.read(b)) != -1) {
						bos.write(b, 0, n);
					}
					fis.close();
					bos.close();

					out.putNextEntry(new ZipEntry("/" + excelFile.getName()));
					out.write(bos.toByteArray());
				}catch(Exception e) {
					e.printStackTrace();
				}finally {
					if(fis != null) {
						fis.close();
					}
					if(bos != null) {
						bos.close();
					}
				}
			}

			// 订单管理身份证地址正反面
			for(Entry<String, List<String>> entry : filePathLitMap.entrySet()) {
				try {
					String folderName = "/" + entry.getKey() + "/";
					out.putNextEntry(new ZipEntry(folderName));

					List<String> filePathLit = entry.getValue();
					if(filePathLit != null && filePathLit.size() > 0) {
						for(int i = 0; i < filePathLit.size(); i ++) {
							FileInputStream fis = null;
							ByteArrayOutputStream bos = null;
							try{
								File file = new File(filePathLit.get(i));
								if(file.exists()) {
									bos = new ByteArrayOutputStream();
									fis = new FileInputStream(file);
									byte[] b = new byte[1024];
									int n;
									while ((n = fis.read(b)) != -1) {
										bos.write(b, 0, n);
									}
									fis.close();
									bos.close();

									String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
									String fileName = String.format("%s%s%s", folderName, i+1, fileExt);
									out.putNextEntry(new ZipEntry(fileName));
									out.write(bos.toByteArray());
								}
							}catch(Exception e) {
								e.printStackTrace();
								continue;
							}finally {
								if(fis != null) {
									fis.close();
								}
								if(bos != null) {
									bos.close();
								}
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
					continue;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(out != null) {
				try {
					out.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
//		try {
//			if (FileUtil.checkFileExists(filePath)) {
//				File file = new File(filePath);
//				// 以流的形式下载文件。
//				InputStream fis = new BufferedInputStream(new FileInputStream(file));
//				byte[] buffer = new byte[fis.available()];
//				fis.read(buffer);
//				fis.close();
//
//				String filename = "idCard-" + DateUtil.formatDate("yyyy-MM-dd", DateUtil.getCurrentDate())
//						+ OrderController.ZIP_FILE_NAME;
//				// 清空response
//				response.reset();
//				// 设置response的Header
//
////				if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
////					filename = URLEncoder.encode(filename, "UTF-8");
////				} else {
////					filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
////				}
//
//				response.addHeader("Content-Disposition", "attachment;filename=" +  new String(filename.getBytes()));
//				response.addHeader("Content-Length", StringUtil.null2Str(file.length()));
//				OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//				response.setContentType("application/octet-stream");
//				toClient.write(buffer);
//				toClient.flush();
//				toClient.close();
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
		
	}
	
	
	/**
	 * 手动下载身份证
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/downLoadIdCard")
	public @ResponseBody void downLoadIdCard(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			String filePath = StringUtil.null2Str(request.getParameter("filePath"));
			if (FileUtil.checkFileExists(filePath)) {
				File file = new File(filePath);
				// 以流的形式下载文件。
				InputStream fis = new BufferedInputStream(new FileInputStream(file));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();

				String filename = "idCard-" + DateUtil.formatDate("yyyy-MM-dd", DateUtil.getCurrentDate())
						+ OrderController.ZIP_FILE_NAME;
				// 清空response
				response.reset();
				// 设置response的Header

				// if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
				// filename = URLEncoder.encode(filename, "UTF-8");
				// } else {
				// filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
				// }

				response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
				response.addHeader("Content-Length", StringUtil.null2Str(file.length()));
				OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
				response.setContentType("application/octet-stream");
				toClient.write(buffer);
				toClient.flush();
				toClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
