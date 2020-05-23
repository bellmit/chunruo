package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.OrderEvaluateListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderEvaluate;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderItemUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 用户评级列表
 * 订单评价列表
 * @author hehai
 */
public class OrderUserEvaluateListTag extends BaseTag  {

	public TagModel<List<OrderEvaluate>> getData(Object pageidx_1, Object pagesize_1, Object lastId_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);

		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<OrderEvaluate>> tagModel = new TagModel<List<OrderEvaluate>>();
		try {
			final UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			// 非代理用户直接返成功
			MsgModel<UserInfo> xmsgModel = PortalUtil.isAgentUserInfo(userInfo);
			if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				tagModel.setCode(PortalConstants.CODE_SUCCESS);
				return tagModel;
			}

			final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			OrderEvaluateListByUserIdCacheManager orderEvaluateListByUserIdCacheManager = Constants.ctx.getBean(OrderEvaluateListByUserIdCacheManager.class);
			
			Map<String, Object> objectMap = new HashMap<String,Object>();
			//分享链接
			String requestURL = RequestUtil.getRequestURL(request);
			String shareUserUrl = requestURL + String.format("/wepage/register?s=%s&c=1", userInfo.getUserId());
			String shareUserWord = "少是零花钱，多是月薪钱，来了就不亏";
			objectMap.put("shareUserUrl", shareUserUrl);							//邀请店长
			objectMap.put("shareUserWord", shareUserWord);							//邀请店长文字
			tagModel.setDataMap(objectMap);
			
			List<Long> evaluateItemIdList = new ArrayList<Long>();
			final Map<String, OrderEvaluate> orderEvaluateMap = orderEvaluateListByUserIdCacheManager.getSession(userInfo.getUserId());
			if (orderEvaluateMap != null && orderEvaluateMap.size() > 0) {
				//已评价列表
				// 排序
				List<Map.Entry<String, OrderEvaluate>> mappingList = new ArrayList<Map.Entry<String, OrderEvaluate>>(orderEvaluateMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, OrderEvaluate>>() {
					public int compare(Map.Entry<String, OrderEvaluate> obj1, Map.Entry<String, OrderEvaluate> obj2) {
						Long recordId1 = StringUtil.nullToLong(obj1.getKey());
						Long recordId2 = StringUtil.nullToLong(obj2.getKey());
						return recordId2.compareTo(recordId1);
					}
				});
				
				//能显示的状态
				List<Integer> statusList = new ArrayList<Integer>();
				statusList.add(OrderEvaluate.EVALUATE_RECORD_STATUS_GOOD); 		// 良好
				statusList.add(OrderEvaluate.EVALUATE_RECORD_STATUS_EXCELLENT); // 精选
				
				// 得到审核通过的评价列表
				for (Map.Entry<String, OrderEvaluate> entry : mappingList) {
					OrderEvaluate evaluate = entry.getValue();
					if (statusList.contains(StringUtil.nullToInteger(evaluate.getStatus()))) {
						evaluateItemIdList.add(evaluate.getItemId());
					}
				}
			}
			
			/**
			 * 自动List分页工具
			 */
			ListPageUtil<OrderEvaluate> pageUtil = new ListPageUtil<OrderEvaluate>() {
				@Override
				public OrderEvaluate addObject(Long objectId) {
					try {
						// 返回对象自定义
						if (orderEvaluateMap.containsKey(StringUtil.null2Str(objectId))) {
							OrderEvaluate evaluate = orderEvaluateMap.get(StringUtil.null2Str(objectId));
							if(evaluate != null && evaluate.getEvaluateId() != null){
								// 商品市场信息
								Order order = orderByIdCacheManager.getSession(evaluate.getOrderId());
								if (order != null 
										&& order.getOrderId() != null 
										&& order.getOrderItemsList() != null
										&& order.getOrderItemsList().size() > 0) {
									List<OrderItems> orderItemList = OrderItemUtil.mergeGroupItems(order.getOrderItemsList());
									for (OrderItems orderItems : orderItemList) {
										if (StringUtil.compareObject(orderItems.getItemId(), evaluate.getItemId())) {
											evaluate.setOrderItems(orderItems);
											break;
										}
									}
								}
								
								//评价用户信息
								String headerImage = StringUtil.null2Str(userInfo.getHeaderImage());
								if (!StringUtil.isNull(headerImage) && !(headerImage.startsWith("http://")
										|| headerImage.startsWith("https://"))) {
									headerImage = RequestUtil.getRequestURL(request) + "/upload/" + headerImage;
								}
								evaluate.setUserHeaderImage(headerImage);
								String nickName = StringUtil.null2Str(userInfo.getNickname());
								String mobile = StringUtil.getMobileFromStr(nickName);
								if(!StringUtil.isNull(mobile)) {
									String mobileFormat = StringUtil.mobileFormat(mobile);
									nickName = nickName.replace(mobile, mobileFormat);
								}
								evaluate.setNickName(nickName);
								
								String content = StringUtil.null2Str(evaluate.getContent());
								String contentMobile = StringUtil.getMobileFromStr(content);
								if(!StringUtil.isNull(contentMobile)) {
									String mobileFormat = StringUtil.mobileFormat(contentMobile);
									content = content.replace(contentMobile, mobileFormat);
								}
								evaluate.setContent(content);
								
								//格式化评价时间
								String evaluateTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, evaluate.getCreateTime());
								evaluate.setEvaluateTime(evaluateTime);
								
								//图片
								List<String> imagePathList = StringUtil.strToStrList(evaluate.getImagePath(), ";");
								evaluate.setImagePathList(imagePathList);
								return evaluate;
							}
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<OrderEvaluate>> listPageVo = pageUtil.getPageList(evaluateItemIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}
			tagModel.setData(listPageVo.getDataList());
			tagModel.setTotalPage(StringUtil.nullToInteger(listPageVo.getPageMax()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;	
	}
}
