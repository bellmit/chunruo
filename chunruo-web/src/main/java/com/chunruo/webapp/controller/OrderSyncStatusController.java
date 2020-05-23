package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.model.OrderSync;
import com.chunruo.core.model.OrderSyncList;
import com.chunruo.core.service.OrderSyncListManager;
import com.chunruo.core.service.OrderSyncManager;
import com.chunruo.core.util.StringUtil;

/**
 * ERP同步状态列表
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/orderSync/")
public class OrderSyncStatusController {
	public Logger log = LoggerFactory.getLogger(OrderSyncStatusController.class);
	@Autowired
	private OrderSyncManager orderSyncManager;
	@Autowired
	private OrderSyncListManager orderSyncListManager;
	
	/**
	 * 同步状态列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/syncList")
	public @ResponseBody Map<String, Object> syncList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<OrderSyncList> orderSyncList = new ArrayList<OrderSyncList> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), OrderSyncList.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.orderSyncListManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				orderSyncList = this.orderSyncListManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", orderSyncList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	/**
	 * 同步状态记录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/syncStatusList")
	public @ResponseBody Map<String, Object> syncStatusList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<OrderSync> orderSyncList = new ArrayList<OrderSync> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), OrderSync.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.orderSyncManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				orderSyncList = this.orderSyncManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", orderSyncList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
}
