package com.chunruo.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.OrderEvaluateListByUserIdCacheManager;
import com.chunruo.core.Constants.OrderStatus;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.OrderEvaluateManager;
import com.chunruo.core.service.OrderItemsManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;

/**
 * 评价
 * @author hehai
 */
@Controller
@RequestMapping("/api/evaluate/")
public class OrderEvaluateController {
	@Autowired
	private OrderItemsManager orderItemsManager;
	@Autowired
	private OrderEvaluateManager orderEvaluateManager;
	@Autowired
	private OrderByIdCacheManager orderByIdCacheManager;
	@Autowired
	private OrderEvaluateListByUserIdCacheManager orderEvaluateListByUserIdCacheManager;
	
	/**
	 * 提交评价信息
	 * @param request
	 * @param response
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/applyEvaluate")
	public @ResponseBody Map<String, Object> applyEvaluate(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderId = StringUtil.nullToLong(request.getParameter("orderId"));
		Long itemId = StringUtil.nullToLong(request.getParameter("itemId"));
		Integer score = StringUtil.nullToInteger(request.getParameter("score"));
		String content = StringUtil.null2Str(request.getParameter("content"));
		String imagePath1 = StringUtil.nullToString(request.getParameter("imagePath1"));
		String imagePath2 = StringUtil.nullToString(request.getParameter("imagePath2"));
		String imagePath3 = StringUtil.nullToString(request.getParameter("imagePath3"));
		String imagePath4 = StringUtil.nullToString(request.getParameter("imagePath4"));
		String imagePath5 = StringUtil.nullToString(request.getParameter("imagePath5"));

		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			Long userId = StringUtil.nullToLong(userInfo.getUserId());
			if (StringUtil.isNull(content)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "内容不能为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if (content.getBytes("UTF-8").length < 18) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "最少评价6个文字");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 得到当前要评价的订单商品
			OrderItems orderItems = null;
			Order order = this.orderByIdCacheManager.getSession(orderId);
			if (order != null && order.getOrderId() != null){
				if(!StringUtil.compareObject(order.getStoreId(), userInfo.getUserId())){
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "抱歉,您无权限操作");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}else if(StringUtil.compareObject(order.getStatus(), OrderStatus.OVER_ORDER_STATUS)) {
					// 检查订单是否已完成,方可进入评价
					List<OrderItems> orderItemsList = order.getOrderItemsList();
					if (orderItemsList != null && orderItemsList.size() > 0) {
						for (OrderItems items : orderItemsList) {
							if (StringUtil.compareObject(items.getItemId(), itemId)) {
								orderItems = items;
								break;
							}
						}
					}
				}
			}
				
			// 检查商品item信息
			if (orderItems == null || orderItems.getItemId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "订单信息不存在错误");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(StringUtil.nullToBoolean(orderItems.getIsEvaluate())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "此商品您已评价,请勿重复提交");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// 检查是否重复提交评论
			if(this.orderEvaluateManager.isExistEvaluateByItemId(itemId)){
				this.orderItemsManager.updateOrderItemsEvaluateStatus(itemId, true);
				try {
					// 清楚缓存
					this.orderByIdCacheManager.removeSession(orderId);
					this.orderEvaluateListByUserIdCacheManager.removeSession(order.getUserId(), order);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "此商品您已评价,请勿重复提交");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			
			StringBuffer imageStrBuf = new StringBuffer();
			if(!StringUtil.isNull(imagePath1)) {
				imageStrBuf.append(imagePath1);
				imageStrBuf.append(";");
			}
			if(!StringUtil.isNull(imagePath2)) {
				imageStrBuf.append(imagePath2);
				imageStrBuf.append(";");
			}
			if(!StringUtil.isNull(imagePath3)) {
				imageStrBuf.append(imagePath3);
				imageStrBuf.append(";");
			}
			if(!StringUtil.isNull(imagePath4)) {
				imageStrBuf.append(imagePath4);
				imageStrBuf.append(";");
			}
			if(!StringUtil.isNull(imagePath5)) {
				imageStrBuf.append(imagePath5);
				imageStrBuf.append(";");
			}
			
			// 保存记录
			OrderEvaluate evaluate = new OrderEvaluate();
			evaluate.setContent(content);
			evaluate.setOrderId(orderId);     // 订单id
			evaluate.setItemId(itemId);       // 订单详情id
			evaluate.setUserId(userId);       // 评价用户id
			evaluate.setScore(score);         // 分数
			if(!StringUtil.isNull(imageStrBuf.toString())){
				evaluate.setImagePath(imageStrBuf.toString().substring(0,imageStrBuf.toString().lastIndexOf(";")));
			}
			evaluate.setStatus(OrderEvaluate.EVALUATE_RECORD_STATUS_WAIT); // 待审核
			evaluate.setProductId(StringUtil.nullToLong(orderItems.getProductId())); // 商品id
			evaluate.setCreateTime(DateUtil.getCurrentDate());
			evaluate.setUpdateTime(evaluate.getCreateTime());
			evaluate = this.orderEvaluateManager.saveOrderEvaluate(evaluate);

			try {
				// 清楚缓存
				this.orderByIdCacheManager.removeSession(orderId);
				this.orderEvaluateListByUserIdCacheManager.removeSession(userId, order);
			} catch (Exception e) {
				e.printStackTrace();
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "评价成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "评价提交失败。");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}
