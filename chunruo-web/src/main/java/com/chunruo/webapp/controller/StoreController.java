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
import com.chunruo.core.model.Bank;
import com.chunruo.core.model.UserAmountChangeRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.service.UserAmountChangeRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/store/")
public class StoreController extends BaseController {
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserWithdrawalManager userWithdrawalManager;
	@Autowired
	private UserAmountChangeRecordManager userAmountChangeRecordManager;

	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserInfo.class);

			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			paramMap.put("isAgent", true);
			count = this.userInfoManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userInfoList = this.userInfoManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(userInfoList != null && userInfoList.size() > 0){
					//????????????entity????????????????????????Managed?????????????????????????????????????????????????????????entity???????????????????????????????????????????????????????????????
					//????????????entity????????????merge????????????????????????
					this.userInfoManager.detach(userInfoList);
					
					Set<Long> topUserIdSet = new HashSet<Long> ();
					for(UserInfo userInfo : userInfoList){
						userInfo.setMobile(StringUtil.mobileFormat(userInfo.getMobile()));
						userInfo.setStoreMobile(StringUtil.mobileFormat(userInfo.getStoreMobile()));
						userInfo.setTopMobile(StringUtil.mobileFormat(userInfo.getTopMobile()));
						userInfo.setSales(StringUtil.nullToDoubleFormat(userInfo.getSales()));
						userInfo.setIncome(StringUtil.nullToDoubleFormat(userInfo.getIncome()));
						userInfo.setBalance(StringUtil.nullToDoubleFormat(userInfo.getBalance()));
						userInfo.setWithdrawalAmount(StringUtil.nullToDoubleFormat(userInfo.getWithdrawalAmount()));
						topUserIdSet.add(userInfo.getTopUserId());
						
						// ???????????????
						if(userInfo.getBankId() != null
								&& Constants.BANK_MAP != null
								&& Constants.BANK_MAP.size() > 0
								&& Constants.BANK_MAP.containsKey(userInfo.getBankId())){
							Bank bank = Constants.BANK_MAP.get(userInfo.getBankId());
							userInfo.setBankName(StringUtil.null2Str(bank.getName()));
						}
					}
					
					// ??????????????????
					List<UserInfo> topUserInfoList = this.userInfoManager.getByIdList(StringUtil.longSetToList(topUserIdSet));
					if(topUserInfoList != null && topUserInfoList.size() > 0){
						Map<Long, String> topUserInfoMap = new HashMap<Long, String> ();
						for(UserInfo topUserInfo : topUserInfoList){
							topUserInfoMap.put(topUserInfo.getUserId(), StringUtil.null2Str(topUserInfo.getStoreName()));
						}
						
						// ?????????????????????
						for(UserInfo userInfo : userInfoList){
							if(userInfo.getTopUserId() != null && topUserInfoMap.containsKey(userInfo.getTopUserId())){
								userInfo.setTopStoreName(topUserInfoMap.get(userInfo.getTopUserId()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userInfoList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	@RequestMapping(value = "/drawalList")
	public @ResponseBody Map<String, Object> drawalList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserWithdrawal> userWithdrawalList = new ArrayList<UserWithdrawal>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserWithdrawal.class);

			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.userWithdrawalManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userWithdrawalList = this.userWithdrawalManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userWithdrawalList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	@RequestMapping(value = "/storeAmountChangeList")
	public @ResponseBody Map<String, Object> storeAmountChangeList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<UserAmountChangeRecord> userAmountChangeRecordList = new ArrayList<UserAmountChangeRecord>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserAmountChangeRecord.class);

			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			String storeName = StringUtil.null2Str(filtersMap.get("storeName"));
			if (!StringUtil.isNull(storeName)) {
				Map<String, Object> storeFiltersMap = new HashMap<String, Object>();
				storeFiltersMap.put("storeName", paramMap.remove("storeName"));
				List<UserInfo> userInfoList = this.userInfoManager.getHqlPages(storeFiltersMap);
				if (!CollectionUtils.isEmpty(userInfoList)) {
					List<Long> userIdList = new ArrayList<Long>();
					for (UserInfo userInfo : userInfoList) {
						userIdList.add(userInfo.getUserId());
					}
					paramMap.put("userId", userIdList);
				}
			}

			count = this.userAmountChangeRecordManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userAmountChangeRecordList = this.userAmountChangeRecordManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if (userAmountChangeRecordList != null && userAmountChangeRecordList.size() > 0) {
					List<Long> userIdList = new ArrayList<Long>();
					for (UserAmountChangeRecord record : userAmountChangeRecordList) {
						Long userId = record.getUserId();
						if (!userIdList.contains(userId)) {
							userIdList.add(userId);
						}
					}

					// ????????????
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo>();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
					if (userInfoList != null && userInfoList.size() > 0) {
						for (UserInfo userInfo : userInfoList) {
							userInfoMap.put(userInfo.getUserId(), userInfo);
						}
					}

					for (UserAmountChangeRecord record : userAmountChangeRecordList) {
						UserInfo userInfo = userInfoMap.get(record.getUserId());
						if (userInfo != null && userInfo.getUserId() != null) {
							record.setStoreName(userInfo.getStoreName());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userAmountChangeRecordList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
}