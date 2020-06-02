package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.model.UserWithdrawalHistory;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserWithdrawalHistoryManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.service.WeChatAppConfigManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.WeiXinPayUtil;
import com.chunruo.core.util.WxSendUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/storeWithdrawal/")
public class UserWithdrawalController extends BaseController {
	
	static Lock lock = new ReentrantLock();
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private WeChatAppConfigManager weChatAppConfigManager;
	@Autowired
	private UserWithdrawalManager userWithdrawalManager;
	@Autowired
	private UserWithdrawalHistoryManager userWithdrawalHistoryManager;
	
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
		List<UserWithdrawal> userWithdrawalList = new ArrayList<UserWithdrawal> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), UserWithdrawal.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.userWithdrawalManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				userWithdrawalList = this.userWithdrawalManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(userWithdrawalList != null && userWithdrawalList.size() > 0){
					List<Long> userIdList = new ArrayList<Long> ();
					for(UserWithdrawal userWithdrawal : userWithdrawalList){
						// 用户ID
						if(userWithdrawal.getUserId() != null && !userIdList.contains(userWithdrawal.getUserId())){
							userIdList.add(userWithdrawal.getUserId());
						}
					}
					
					// 所有订单
					Map<Long, UserInfo> userInfoMap = new HashMap<Long, UserInfo> ();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
					if(userInfoList != null && userInfoList.size() > 0){
						for(UserInfo userInfo : userInfoList){
							userInfoMap.put(userInfo.getUserId(), userInfo);
						}
					}
					
					// 补店铺名称\用户名称\仓库名称\分类
					for(UserWithdrawal userWithdrawal : userWithdrawalList){
						if(userInfoMap.containsKey(userWithdrawal.getUserId())){
							userWithdrawal.setUserName(userInfoMap.get(userWithdrawal.getUserId()).getNickname());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", userWithdrawalList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	/**
	 * 提现详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getStoreWithdrawalById")
	public @ResponseBody Map<String, Object> getStoreWithdrawalById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long recordId = StringUtil.nullToLong(request.getParameter("recordId"));
		UserWithdrawal userWithdrawal = new UserWithdrawal();
		List<UserWithdrawalHistory> userWithdrawalHistoryList = new ArrayList<UserWithdrawalHistory> ();
		try {
			userWithdrawal = this.userWithdrawalManager.get(recordId);
			if (userWithdrawal != null && userWithdrawal.getRecordId() != null) {
				
				UserInfo userInfo = this.userInfoManager.get(userWithdrawal.getUserId());
				if(userInfo != null && userInfo.getUserId() != null){
					userWithdrawal.setUserName(userInfo.getNickname());
				}
				userWithdrawalHistoryList = this.userWithdrawalHistoryManager.getUserWithdrawalHistoryListByRecordId(userWithdrawal.getRecordId());
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("userWithdrawal", userWithdrawal);
		resultMap.put("userWithdrawalHistoryList", userWithdrawalHistoryList);
		return resultMap;
	}
	/**
	 * 提现成功
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/withdrawalSucc")
	public @ResponseBody Map<String, Object> withdrawalSucc(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long recordId = StringUtil.nullToLong(request.getParameter("recordId"));
		lock.lock();
		try {
			UserWithdrawal userWithdrawal = this.userWithdrawalManager.get(recordId);
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(UserWithdrawal.USER_WITHDRAWAL_STATUS_FAIL);
			statusList.add(UserWithdrawal.USER_WITHDRAWAL_STATUS_SUCC);
			if(statusList.contains(StringUtil.nullToInteger(userWithdrawal.getStatus()))) {
				resultMap.put("success", false);
				resultMap.put("message", "你已提交过，请勿重复提交");
				return resultMap;
			}
			if (userWithdrawal != null && userWithdrawal.getRecordId() != null) {
				//提现
				WeChatAppConfig weChatAppConfig = this.weChatAppConfigManager.get(1L);
				String tradeNo = CoreInitUtil.getRandomNo();
				
				UserInfo userInfo = this.userInfoManager.get(StringUtil.nullToLong(userWithdrawal.getUserId()));
				if(userInfo == null || userInfo.getUserId() == null
						|| StringUtil.isNull(userInfo.getOpenId())) {
					resultMap.put("success", false);
					resultMap.put("message", "用户openid为空");
					return resultMap;
				}
				
				String openid = StringUtil.null2Str(userInfo.getOpenId());
				MsgModel<Boolean> msgModel = WeiXinPayUtil.transfers(weChatAppConfig, tradeNo, StringUtil.nullToDouble(userWithdrawal.getAmount()), openid, userWithdrawal.getName());
				if(!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
					resultMap.put("success", false);
					resultMap.put("message", StringUtil.null2Str(msgModel.getMessage()));
					return resultMap;
				}
				
				userWithdrawal.setTradeNo(tradeNo);
				userWithdrawal.setStatus(UserWithdrawal.USER_WITHDRAWAL_STATUS_SUCC);
				userWithdrawal.setComplateTime(DateUtil.getCurrentDate());
				userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
				
				Long userId = this.getUserId(request);
				String title = "提现成功";
				String message = String.format("[%s]操作提现记录，提现成功状态", StringUtil.null2Str(this.getUserName(request)));
				this.userWithdrawalManager.saveUserWithdrawal(userWithdrawal, userId, title, message);
				
				
				try {
					WxSendUtil.withdrawalSucc(StringUtil.nullToDoubleFormat(userWithdrawal.getAmount()), userWithdrawal.getTradeNo(), userInfo);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}finally {
			lock.unlock();
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	/**
	 * 提现失败
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/withdrawalFail")
	public @ResponseBody Map<String, Object> withdrawalFail(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long recordId = StringUtil.nullToLong(request.getParameter("recordId"));
		String replyMsg = StringUtil.nullToString(request.getParameter("replyMsg"));
		try {
			if (StringUtil.isNullStr(replyMsg)) {
				resultMap.put("success", false);
				resultMap.put("message", "请填写失败原因");
				return resultMap;
			}
			
			UserWithdrawal userWithdrawal = this.userWithdrawalManager.get(recordId);
			List<Integer> statusList = new ArrayList<Integer>();
			statusList.add(UserWithdrawal.USER_WITHDRAWAL_STATUS_FAIL);
			statusList.add(UserWithdrawal.USER_WITHDRAWAL_STATUS_SUCC);
			if(statusList.contains(StringUtil.nullToInteger(userWithdrawal.getStatus()))) {
				resultMap.put("success", false);
				resultMap.put("message", "你已提交过，请勿重复提交");
				return resultMap;
			}
			if (userWithdrawal != null && userWithdrawal.getRecordId() != null) {
				userWithdrawal.setStatus(UserWithdrawal.USER_WITHDRAWAL_STATUS_FAIL);
				userWithdrawal.setComplateTime(DateUtil.getCurrentDate());
				userWithdrawal.setUpdateTime(DateUtil.getCurrentDate());
				
				Long userId = this.getUserId(request);
				String title = "提现失败";
				String message = String.format("[%s]操作提现记录，提现失败状态", StringUtil.null2Str(this.getUserName(request)));
				this.userWithdrawalManager.saveUserWithdrawal(userWithdrawal, userId, title, message);
			}
			
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
}
