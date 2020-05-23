package com.chunruo.portal.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.cache.portal.impl.UserWithdrawalListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserProfitRecordManager;
import com.chunruo.core.service.UserWithdrawalManager;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;

@Controller
@RequestMapping("/api/withdrawal")
public class WithdrawalController extends BaseController{
	
	static Lock lock = new ReentrantLock();
	@Autowired
	private UserWithdrawalManager userWithdrawalManager;
	@Autowired
	private UserInfoByIdCacheManager userInfoByIdCacheManager;
	@Autowired
	private UserWithdrawalListByUserIdCacheManager userWithdrawalListByUserIdCacheManager;
	
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/drawal")
	public @ResponseBody Map<String, Object> drawal(final HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
		
		lock.lock();
		try {
			Double amount = StringUtil.nullToDoubleFormat(request.getParameter("amount"));           //提现金额（含个税）
			
			// 检查提现金额大于100
			if (amount < 100) {
				resultMap.put(PortalConstants.SYSTEMTIME, System.currentTimeMillis());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "提现金额必须不小于100元");
				return resultMap;
			}
			
			//提现金额信息检查
			MsgModel<UserInfo> cMsgModel = WithdrawalController.checkAmountInfo(userInfo,amount);
			if(!StringUtil.nullToBoolean(cMsgModel.getIsSucc())) {
				resultMap.put(PortalConstants.SYSTEMTIME, System.currentTimeMillis());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, cMsgModel.getMessage());
				return resultMap;
			}
			
			UserInfo realUserInfo = cMsgModel.getData();
			
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("userId", userInfo.getUserId());
			paramMap.put("amount", amount);
			
			// 提现
			MsgModel<Double> msgMode = this.userWithdrawalManager.insertUserDrawalRecord(paramMap);
			if(!StringUtil.nullToBoolean(msgMode.getIsSucc())){
				// 提现失败直接返回
				resultMap.put("amount", StringUtil.nullToDoubleFormatStr(realUserInfo.getBalance()));
				resultMap.put(PortalConstants.SYSTEMTIME, System.currentTimeMillis());
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, msgMode.getMessage());
				return resultMap;
			}
			
			try{
				// 更新缓存数据
				this.userInfoByIdCacheManager.removeSession(userInfo.getUserId());
				this.userWithdrawalListByUserIdCacheManager.removeSession(userInfo.getUserId());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			resultMap.put("amount", msgMode.getData());
			resultMap.put(PortalConstants.SYSTEMTIME, System.currentTimeMillis());
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "提现成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 释放锁
			lock.unlock();     
		}
		
		resultMap.put(PortalConstants.SYSTEMTIME, System.currentTimeMillis());
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "提现失败");
		return resultMap;
	}
	
	
	public static MsgModel<UserInfo> checkAmountInfo(UserInfo userInfo, Double amount){
		MsgModel<UserInfo> msgModel = new MsgModel<UserInfo>();
		try {
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
			UserProfitRecordManager userProfitRecordManager = Constants.ctx.getBean(UserProfitRecordManager.class);
			UserWithdrawalManager userWithdrawalManager = Constants.ctx.getBean(UserWithdrawalManager.class);

			UserInfo realUserInfo = userInfoManager.get(userInfo.getUserId());
			Double balance = StringUtil.nullToDoubleFormat(realUserInfo.getBalance());
			if (amount.compareTo(balance) == 1) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("金额错误，大于可提现金额");
				return msgModel;
			}

			Double totalProfitIncome = userProfitRecordManager.countUserProfitTotalIncomeByUserId(userInfo.getUserId());
		
			if (amount.compareTo(totalProfitIncome) == 1) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("提现金额异常");
				return msgModel;
			}

			Double totalWithdrawal = userWithdrawalManager.countUserWithdrawalTotalIncomeByUserId(userInfo.getUserId());
			totalWithdrawal = StringUtil.nullToDoubleFormat(totalWithdrawal);
			if (totalWithdrawal.compareTo(totalProfitIncome) == 1) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("提现金额异常");
				return msgModel;
			}
			
            // 总结算利润误差在30元范围内
			Double allProfitIncome = DoubleUtil.add(totalWithdrawal, balance);
			if (!StringUtil.doubleTowValueBetween(allProfitIncome, totalProfitIncome, Double.valueOf(30))) {
				// 总结算利润误差在30元范围内
				msgModel.setIsSucc(false);
				msgModel.setMessage("提现金额异常");
				return msgModel;
			}

			msgModel.setIsSucc(true);
			msgModel.setData(realUserInfo);
			return msgModel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("服务器错误");
		return msgModel;
	}
}
