package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.OrderWaitEvaluateListByStoreIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderItemUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 待评价
 * 订单商品明细
 * @author chunruo
 *
 */
public class OrderWaitEvaluateListTag extends BaseTag{

	public TagModel<List<OrderItems>> getData(Object pageidx_1, Object pagesize_1, Object lastId_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);

		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<OrderItems>> tagModel = new TagModel<List<OrderItems>>();
		try {
			// 检查用户是否登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null) {
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
			
			// 商品评价广告位图片
			Long channeEvaluatelId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.order.evaluate.id"));
//			MsgModel<List<Object>> xmgModel = ProductSeckillListTag.getSeckillRecommendImageList(channeEvaluatelId);
//			if(StringUtil.nullToBoolean(xmgModel.getIsSucc())){
//				tagModel.setDataList(xmgModel.getData());
//			}

			// 已完成状态商品信息
			List<OrderItems> waitEvaluateItemsList = getOrderWaitEvaluateList(userInfo.getUserId());

			// 得到所有的评价列表
			List<Long> itemIdList = new ArrayList<Long> ();
			final Map<Long, OrderItems> orderItemsMap = new HashMap<Long, OrderItems> ();
			if(waitEvaluateItemsList != null && waitEvaluateItemsList.size() > 0){
				// 排序
				Collections.sort(waitEvaluateItemsList, new Comparator<OrderItems>() {
					public int compare(OrderItems obj1, OrderItems obj2) {
						Long itemId_1= StringUtil.nullToLong(obj1.getItemId());
						Long itemId_2 = StringUtil.nullToLong(obj2.getItemId());
						return itemId_2.compareTo(itemId_1);
					}
				});

				for(OrderItems orderItems : waitEvaluateItemsList){
					itemIdList.add(orderItems.getItemId());
					orderItemsMap.put(orderItems.getItemId(), orderItems);
				}
			}

			/**
			 * 自动List分页工具
			 */
			ListPageUtil<OrderItems> pageUtil = new ListPageUtil<OrderItems>() {
				@Override
				public OrderItems addObject(Long objectId) {
					// 返回对象自定义
					return orderItemsMap.get(StringUtil.nullToLong(objectId));
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<OrderItems>> listPageVo = pageUtil.getPageList(itemIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}

			tagModel.setData(listPageVo.getDataList());
			tagModel.setTotalPage(StringUtil.nullToInteger(listPageVo.getPageMax()));

			Map<String, Object> objectMap = new HashMap<String,Object>();
			tagModel.setDataMap(objectMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
	
	/**
	 * 获取用户待评价列表
	 * @param storeId
	 * @return
	 */
	public static List<OrderItems> getOrderWaitEvaluateList(Long storeId){
		List<OrderItems> waitEvaluateItemsList = new ArrayList<OrderItems> ();
		try {
			OrderWaitEvaluateListByStoreIdCacheManager orderWaitEvaluateListByStoreIdCacheManager = Constants.ctx.getBean(OrderWaitEvaluateListByStoreIdCacheManager.class);
			Map<String,List<OrderItems>> orderWaitEvaluteMap = orderWaitEvaluateListByStoreIdCacheManager.getSession(storeId);
		    if(orderWaitEvaluteMap != null && !orderWaitEvaluteMap.isEmpty()) {
		    	for(Map.Entry<String, List<OrderItems>> entry : orderWaitEvaluteMap.entrySet()) {
		    		List<OrderItems> orderItemsList = OrderItemUtil.mergeGroupItems(entry.getValue());
					for(OrderItems orderItems : orderItemsList){
						// 待评价订单商品明细
						waitEvaluateItemsList.add(orderItems);
					}
		    	}
		    }
		}catch(Exception e) {
			e.printStackTrace();
		}
	    return waitEvaluateItemsList;
	}
}
