package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.RefundByOrderItemIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
import com.chunruo.core.model.RollingNotice;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderItemUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 退款退货详情页面
 * @author liujun
 *
 */
public class RefundDetailTag extends BaseTag {

	public TagModel<Refund> getData(Object orderItemId_1) {
		Long orderItemId = StringUtil.nullToLong(orderItemId_1);
		TagModel<Refund> tagModel = new TagModel<>();
		if (orderItemId == null || StringUtil.compareObject(orderItemId, 0)) {
			tagModel.setCode(PortalConstants.CODE_ERROR);
			tagModel.setMsg("参数错误");
			return tagModel;
		}
		
		try {
			final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			RefundByOrderItemIdCacheManager refundByOrderItemIdCacheManager = Constants.ctx.getBean(RefundByOrderItemIdCacheManager.class);
			
			// 检查用户是否登录状态
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			// 退款信息
			Refund refund = refundByOrderItemIdCacheManager.getSession(orderItemId);
			if(refund != null ){
				//商品市场信息
				Order order = orderByIdCacheManager.getSession(refund.getOrderId());
				if (order != null 
						&& order.getOrderId() != null
						&& order.getOrderItemsList() != null
						&& order.getOrderItemsList().size() > 0) {
					List<OrderItems> orderItemList = OrderItemUtil.mergeGroupItems(order.getOrderItemsList());
					for(OrderItems orderItems : orderItemList){
						if(StringUtil.compareObject(orderItems.getItemId(), refund.getOrderItemId())){
							refund.setOrderItems(orderItems);
							break;
						}
					}
				}
				
				// 退款退货原因
				refund.setReason(Constants.REFUND_REASON_MAP.get(refund.getReasonId()).getReason());
				
				tagModel.setMapList(this.initRefundSpeed(refund));
				tagModel.setDataMap(this.getRefundContent(refund));
				tagModel.setData(refund);
				tagModel.setCode(PortalConstants.CODE_SUCCESS);
				tagModel.setMsg("操作成功");
				return tagModel;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("refund detail error ===== > " + e.toString());
			tagModel.setCode(PortalConstants.CODE_ERROR);
			tagModel.setMsg("系统异常，请稍后重试");
			return tagModel;
		}
		
		
		tagModel.setCode(PortalConstants.CODE_ERROR);
		tagModel.setMsg("请求信息不存在");
		return tagModel;
	}

	/**
	 * 退款退货流程
	 * @param refundType
	 * @param status
	 * @return
	 */
	private List<Map<String, Object>> initRefundSpeed(Refund refund) {
		List<Map<String, Object>> speedList = new ArrayList<Map<String, Object>> ();
		
		// 退款退货申请关闭状态
		List<Integer> closeStatusList = new ArrayList<Integer> ();
		closeStatusList.add(Refund.REFUND_STATUS_REFUSE);	//审核被拒
		closeStatusList.add(Refund.REFUND_STATUS_TIMEOUT);	//申请已超时
		
		int index = 1;
		if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_MONEY)
				|| StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_PART)){
			//步骤一:填写申请
			Map<String, Object> step1 = new HashMap<String, Object> ();
			step1.put("name", "填写申请");
			step1.put("sort", index++);
			step1.put("status", 1);
			speedList.add(step1);
			
			//步骤二:平台审核
			Map<String, Object> step2 = new HashMap<String, Object> ();
			step2.put("name", "平台审核");
			step2.put("sort", index++);
			step2.put("status", 1);
			speedList.add(step2);
			
			List<Integer> successStatusList = new ArrayList<Integer> ();
			successStatusList.add(Refund.REFUND_STATUS_SUCCESS);	//退货审核通过
			successStatusList.add(Refund.REFUND_STATUS_COMPLETED);	//退款完成
			
			//步骤三:退款结果
			if(successStatusList.contains(refund.getRefundStatus())){
				//退款完成
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "退款完成");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
			}else if(closeStatusList.contains(refund.getRefundStatus())){
				//售后关闭
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "售后关闭");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
				return speedList;
			}else{
				//退款处理中
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "退款完成");
				step.put("sort", index++);
				step.put("status", 0);
				speedList.add(step);
			}
		}else if(StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_GOODS)){
			
			//步骤一:填写申请
			Map<String, Object> step1 = new HashMap<String, Object> ();
			step1.put("name", "填写申请");
			step1.put("sort", index++);
			step1.put("status", 1);
			speedList.add(step1);
			
			//步骤二:平台审核
			Map<String, Object> step2 = new HashMap<String, Object> ();
			step2.put("name", "平台审核");
			step2.put("sort", index++);
			step2.put("status", 1);
			speedList.add(step2);
			
			//步骤三:寄回商品
			if(StringUtil.nullToBoolean(refund.getIsReceive()) || StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_SUCCESS)){
				//寄回商品
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "寄回商品");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
			}else{
				//寄回商品
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "寄回商品");
				step.put("sort", index++);
				step.put("status", 0);
				speedList.add(step);
			}
			
			//步骤四:平台收货
			if(StringUtil.nullToBoolean(refund.getIsReceive()) || StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_RECEIPT)){
				//平台收货
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "平台收货");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
			}else{
				//平台收货中
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "平台收货");
				step.put("sort", index++);
				step.put("status", 0);
				speedList.add(step);
			}
			
			//步骤五:退款结果
			if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_COMPLETED)){
				//退款完成
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "退款完成");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
			}else if(closeStatusList.contains(refund.getRefundStatus())){
				//售后关闭
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "售后关闭");
				step.put("sort", index++);
				step.put("status", 1);
				speedList.add(step);
			}else{
				//退款处理中
				Map<String, Object> step = new HashMap<String, Object> ();
				step.put("name", "退款完成");
				step.put("sort", index++);
				step.put("status", 0);
				speedList.add(step);
			}
		}
		return speedList;
	}
	
	/**
	 * 售后进度内容
	 * @param refund
	 * @return
	 */
	private Map<String, Object> getRefundContent(Refund refund) {
		Map<String, Object> map = new HashMap<>();
		String dataTime = DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, refund.getUpdateTime());
		int timeOutDays = StringUtil.nullToInteger(Constants.conf.getProperty("refund_express_date"));
	
		// 滚动通知信息
		RollingNotice rollingNotice = Constants.ROLLING_NOTICE_MAP.get(RollingNotice.ROLLING_NOTICE_FRAUD);
		if(rollingNotice != null && rollingNotice.getNoticeId() != null){
			map.put("rollingNotice",StringUtil.null2Str(rollingNotice.getContent()));
		}
		if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_WAIT)){
			//申请中
			map.put("title", "售后申请已提交");
			map.put("content", "您的售后申请已提交，客服将为您及时解决");
			map.put("refundTimeTitle", String.format("申请时间: %s", dataTime));
			return map;
		}else if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_REFUSE)){
			//审核被拒
			map.put("title", "售后申请未通过");
			map.put("content", StringUtil.null2Str(refund.getRefusalReason()));     
			map.put("refundTimeTitle", String.format("审核驳回时间: %s", dataTime));
			return map;
		}else{
			if(StringUtil.compareObject(Refund.REFUND_TYPE_MONEY, refund.getRefundType())
					|| StringUtil.compareObject(Refund.REFUND_TYPE_PART, refund.getRefundType())){
				
//				List<Integer> waitStatusList = new ArrayList<Integer>();
//				waitStatusList.add(Refund.REFUND_STATUS__MANAGER);//客服主管审核
//				waitStatusList.add(Refund.REFUND_STATUS_FINANCE);//财务审核
//				waitStatusList.add(Refund.REFUND_STATUS_REJECT);//被驳回
//				if(waitStatusList.contains(refund.getRefundStatus())) {
//					//申请中
//					map.put("title", "售后申请已提交");
//					map.put("content", "您的售后申请已提交，客服将为您及时解决");
//					map.put("refundTimeTitle", String.format("申请时间: %s", dataTime));
//					return map;
//				}
				//退款类型
				List<Integer> successStatusList = new ArrayList<Integer> ();
				successStatusList.add(Refund.REFUND_STATUS_SUCCESS);	//退货审核通过
				successStatusList.add(Refund.REFUND_STATUS_COMPLETED);	//退款完成
				if (successStatusList.contains(refund.getRefundStatus())) {
					// 同意退款
//					String highLight = "1-3工作日";
					map.put("title", "售后申请已通过");
					map.put("content", "退款申请已同意，退款将原路返回");
					map.put("refundTimeTitle", String.format("退款时间: %s", dataTime));
//					map.put("highlight", highLight);
					return map;
				}
				
			    if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_TIMEOUT)){
					//申请已超时
					map.put("title", "售后申请未通过");
					map.put("content", "您的退款请求未通过。");
					map.put("refundTimeTitle", String.format("审核驳回时间: %s", dataTime));
					return map;
				}
			}else if(StringUtil.compareObject(Refund.REFUND_TYPE_GOODS, refund.getRefundType())){
				//退款类型
				List<Integer> receiptStatusList = new ArrayList<Integer> ();
				receiptStatusList.add(Refund.REFUND_STATUS_RECEIPT);	//平台收货
//				receiptStatusList.add(Refund.REFUND_STATUS__MANAGER);	//客服主管审核
//				receiptStatusList.add(Refund.REFUND_STATUS_FINANCE);	//财务审核
//				receiptStatusList.add(Refund.REFUND_STATUS_REJECT);	//被驳回
				//退货类型
				if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_SUCCESS)){
					//退货审核通过
					String highLight = DateUtil.formatDayHourMinute(timeOutDays,refund.getUpdateTime());
					map.put("title", "填写物流单号");
					map.put("content", String.format("卖家同意了这次退货申请，请邮寄到以下退货地址:%s，如您在%s内未处理，申请将自动过期.", StringUtil.null2Str(refund.getRefundAddress()), highLight));
					//map.put("refundTimeTitle", String.format("审核通过时间: %s", dataTime));
					map.put("copyAddress", StringUtil.null2Str(refund.getRefundAddress()));
					map.put("highlight", highLight);
					return map;
				}else if(receiptStatusList.contains(refund.getRefundStatus())){
					//平台收货
					map.put("title", "已填写运单号，请耐心等候");
					map.put("content", "买家已退货，客服将为您及时解决");
				//	map.put("refundTimeTitle", String.format("平台收货时间: %s", dataTime));
					return map;
				}else if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_COMPLETED)){
					//退款完成
					String highLight = "1-3工作日";
					map.put("title", "售后申请已通过");
					map.put("content", "退款申请已同意，退款将原路返回");
					map.put("refundTimeTitle", String.format("退款时间: %s", dataTime));
					map.put("highlight", highLight);
					return map;
				}else if(StringUtil.compareObject(refund.getRefundStatus(), Refund.REFUND_STATUS_TIMEOUT)){
					//申请已超时
					map.put("title", "售后申请未通过");
					map.put("content", "您申请退款退货流程超时，请再重新申请。");
					map.put("refundTimeTitle", String.format("审核驳回时间: %s", dataTime));
					return map;
				}
			}
		}
		return map;
	}
}
