package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/userProfitRecord/")
public class UserProfitRecordController extends BaseController {
	@Autowired
	private OrderManager orderManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserProfitRecordManager userProfitRecordManager;
	
	/**
	 * 订单利润列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserProfitRecord> userProfitRecordList = new ArrayList<UserProfitRecord> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserProfitRecord.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.userProfitRecordManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userProfitRecordList = this.userProfitRecordManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(userProfitRecordList != null && userProfitRecordList.size() > 0){
					Set<Long> orderIdSet = new HashSet<Long> ();
					Set<Long> userIdSet = new HashSet<Long> ();
					for(UserProfitRecord userProfitRecord : userProfitRecordList){
						// 订单ID
						orderIdSet.add(userProfitRecord.getOrderId());
						// 店铺ID
						userIdSet.add(userProfitRecord.getUserId());
						userIdSet.add(userProfitRecord.getFromUserId());
					}
					
					// 所有订单
					Map<Long, Order> orderMap = new HashMap<Long, Order> ();
					List<Order> orderList = this.orderManager.getByIdList(StringUtil.longSetToList(orderIdSet));
					if(orderList != null && orderList.size() > 0){
						for(Order order : orderList){
							orderMap.put(order.getOrderId(), order);
						}
					}
					
					// 所有店铺
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo> ();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(StringUtil.longSetToList(userIdSet));
					if(userInfoList != null && userInfoList.size() > 0){
						for(UserInfo userInfo : userInfoList){
							userInfoMap.put(userInfo.getUserId(), userInfo);
						}
					}
					
					// 补店铺名称\用户名称\仓库名称\分类
					for(UserProfitRecord userProfitRecord : userProfitRecordList){
						if(orderMap.containsKey(userProfitRecord.getOrderId())){
							userProfitRecord.setOrderAmount(orderMap.get(userProfitRecord.getOrderId()).getOrderAmount());
						}
						
						// 店铺信息
						if(userInfoMap.containsKey(userProfitRecord.getUserId())){
							userProfitRecord.setStoreName(userInfoMap.get(userProfitRecord.getUserId()).getStoreName());
						}
						
						// 来源店铺信息
						if(userInfoMap.containsKey(userProfitRecord.getFromUserId())){
							userProfitRecord.setFromStoreName(userInfoMap.get(userProfitRecord.getFromUserId()).getStoreName());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userProfitRecordList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
}
