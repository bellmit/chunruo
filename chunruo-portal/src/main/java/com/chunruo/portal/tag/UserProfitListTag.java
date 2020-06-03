package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.cache.portal.impl.UserProfitByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.core.vo.RefundVo;
import com.chunruo.portal.vo.TagModel;

public class UserProfitListTag extends BaseTag {

	public TagModel<List<UserProfitRecord>> getData(Object status_1, Object pageidx_1, Object pagesize_1, Object lastId_1){
		Integer status = UserProfitRecord.DISTRIBUTION_STATUS_SUCC; 		//结算状态(1:交易中;3:已结算)
		Integer pageidx = StringUtil.nullToInteger(pageidx_1); 
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
		
		//设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;
		
		TagModel<List<UserProfitRecord>> tagModel = new TagModel<List<UserProfitRecord>> ();
		try{
			// 检查用户是否登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null){
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			
			final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			final UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			final UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
			final RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
			Map<String, UserProfitRecord> userProfitIdMap = userProfitByUserIdCacheManager.getSession(userInfo.getUserId());
			if(userProfitIdMap == null || userProfitIdMap.size() <= 0){
				tagModel.setData(new ArrayList<UserProfitRecord>());
				tagModel.setCode(PortalConstants.CODE_SUCCESS);
				return tagModel;
			}
			
			List<Map.Entry<String, UserProfitRecord>> mappingList = new ArrayList<Map.Entry<String, UserProfitRecord>> (userProfitIdMap.entrySet());
			Collections.sort(mappingList, new Comparator<Map.Entry<String, UserProfitRecord>>(){
				public int compare(Map.Entry<String, UserProfitRecord> obj1, Map.Entry<String, UserProfitRecord> obj2){
					Long recordId1 = StringUtil.nullToLong(obj1.getKey());
					Long recordId2 = StringUtil.nullToLong(obj2.getKey());
					return (recordId1.longValue() < recordId2.longValue()) ? 1 : -1;
				}
			});
			
			Double totalBalance = new Double(0);
			List<Long> recordIdList = new ArrayList<Long>();
			Map<Long, UserProfitRecord> recordIdBeanMap = new HashMap<Long, UserProfitRecord>();
			for(Map.Entry<String, UserProfitRecord> entry : mappingList){
				UserProfitRecord storeProfitRecord = entry.getValue();
				if(storeProfitRecord == null || storeProfitRecord.getRecordId() == null){
					continue;
				}
				
				Integer statusBak = StringUtil.nullToInteger(storeProfitRecord.getStatus());	
				if(status.equals(UserProfitRecord.DISTRIBUTION_STATUS_SUCC)){
					if(!StringUtil.compareObject(status, statusBak) && !StringUtil.compareObject(UserProfitRecord.DISTRIBUTION_STATUS_RETURN, statusBak)){
						continue;
					}
				}else if(!StringUtil.compareObject(status, statusBak) && StringUtil.compareObject(status, UserProfitRecord.DISTRIBUTION_STATUS_INIT)){
					continue;
				}else if(StringUtil.compareObject(UserProfitRecord.DISTRIBUTION_STATUS_RETURN, statusBak)
						&& !StringUtil.compareObject(status, UserProfitRecord.DISTRIBUTION_STATUS_SUCC)) {
					//退款关闭显示在已结算
					continue;
				}
				
				// 本次订单利润
				Double defultIncome = storeProfitRecord.getIncome();
				if(StringUtil.compareObject(storeProfitRecord.getStatus(), UserProfitRecord.DISTRIBUTION_STATUS_RETURN)){
					defultIncome = 0.0D;
					storeProfitRecord.setIncome(defultIncome);
				}
				
				totalBalance = totalBalance + defultIncome;
				recordIdList.add(storeProfitRecord.getRecordId());
				recordIdBeanMap.put(storeProfitRecord.getRecordId(), storeProfitRecord);
			}
			
			final String requestURL = this.getRequestURL(request);
			final Map<Long, UserProfitRecord> recordIdBeanMapBak = recordIdBeanMap;
			
			
			/**
			 * 自动List分页工具
			 */
			ListPageUtil<UserProfitRecord> pageUtil = new ListPageUtil<UserProfitRecord> (){
				@Override
				public UserProfitRecord addObject(Long objectId) {
					// 返回对象自定义
					if(recordIdBeanMapBak != null && recordIdBeanMapBak.containsKey(objectId)){
						UserProfitRecord userProfitRecord = recordIdBeanMapBak.get(objectId);
						Order order = orderByIdCacheManager.getSession(userProfitRecord.getOrderId());
						if(order == null || CollectionUtils.isEmpty(order.getOrderItemsList())){
							return null;
						}
						boolean isHaveRefund = false;
						Double orderAmount = order.getOrderAmount();
						Double orderProfit = StringUtil.nullToDouble(order.getProfitTop());
						Double refundProfit = 0.0d;  //退款利润
						
						List<OrderItems> itemList = new ArrayList<OrderItems>();
						List<RefundVo> refundVoList = new ArrayList<RefundVo>();
						List<OrderItems> orderItemsList =  order.getOrderItemsList();
						if(orderItemsList != null && orderItemsList.size() > 0){
							for(OrderItems item : orderItemsList){
								item.setRefundStatus(0);
								Refund refund = refundByOrderItemIdCacheManager.getSession(item.getItemId());
								if(refund != null && refund.getRefundId() != null){
									item.setRefundStatus(refund.getRefundStatus());
									if(StringUtil.compareObject(Refund.REFUND_STATUS_COMPLETED, refund.getRefundStatus())) {
										isHaveRefund = true;
										Double itemRefundProfit = StringUtil.nullToDoubleFormat(item.getTopProfit());
										refundProfit += StringUtil.nullToDoubleFormat(itemRefundProfit);
										RefundVo refundVo = new RefundVo();
										refundVo.setRefundProfit(StringUtil.nullToDouble(itemRefundProfit));
										refundVo.setCreateTime(refund.getCreateTime());
										refundVoList.add(refundVo);
									}
								}
								itemList.add(item);
							}
						}
						
						String nickName = "未知名";
						UserInfo userInfo = userInfoByIdCacheManager.getSession(userProfitRecord.getFromUserId());
						if(userInfo != null && userInfo.getUserId() != null){
							nickName = StringUtil.null2Str(userInfo.getNickname());
						}
						
						userProfitRecord.setOrderProfit(StringUtil.nullToDouble(orderProfit));
						userProfitRecord.setRefundVoList(refundVoList);
						userProfitRecord.setIsHaveRefund(isHaveRefund);
						userProfitRecord.setRefundProfit(refundProfit);
						userProfitRecord.setNickName(nickName);
						userProfitRecord.setOrderAmount(orderAmount);
						userProfitRecord.setOrderItemsList(itemList);
						return userProfitRecord;
					}
					return null;
				}
			};
			
			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<UserProfitRecord>> listPageVo = pageUtil.getPageList(recordIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer (requestURL + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("status=" + status + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}
			tagModel.setData(listPageVo.getDataList());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}
