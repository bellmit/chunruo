package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.cache.portal.impl.OrderByIdCacheManager;
import com.chunruo.cache.portal.impl.RefundListByStoreIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Refund;
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
 * 退款退货记录列表
 * @author chunruo
 *
 */
public class RefundStoreListTag extends BaseTag {

	public TagModel<List<Refund>> getData(Object pageidx_1, Object pagesize_1, Object lastId_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);

		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<Refund>> tagModel = new TagModel<List<Refund>> ();
		try{
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}

			List<Long> refundIdList = new ArrayList<Long>();
			final Map<Long, Refund> map = new HashMap<Long, Refund>();
			final OrderByIdCacheManager orderByIdCacheManager = Constants.ctx.getBean(OrderByIdCacheManager.class);
			RefundListByStoreIdCacheManager refundListByStoreIdCacheManager = Constants.ctx.getBean(RefundListByStoreIdCacheManager.class);

			MsgModel<UserInfo> xmsgModel = PortalUtil.isAgentUserInfo(userInfo);
			if (StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				// 代理商才有退款订单信息显示
				Map<String, Refund> refundMap = refundListByStoreIdCacheManager.getSession(userInfo.getUserId());
				if (refundMap != null && refundMap.size() > 0) {
					// 排序
					List<Map.Entry<String, Refund>> mappingList = new ArrayList<Map.Entry<String, Refund>>(refundMap.entrySet());
					Collections.sort(mappingList, new Comparator<Map.Entry<String, Refund>>() {
						public int compare(Map.Entry<String, Refund> obj1, Map.Entry<String, Refund> obj2) {
							Long refundId1 = StringUtil.nullToLong(obj1.getKey());
							Long refundId2 = StringUtil.nullToLong(obj2.getKey());
							return (refundId1.longValue() < refundId2.longValue()) ? 1 : -1;
						}
					});

					for (Map.Entry<String, Refund> entry : mappingList) {
						if(StringUtil.compareObject(entry.getValue().getRefundType(), Refund.REFUND_TYPE_CANCEL)) {
							continue;
						}
						Long refundId = StringUtil.nullToLong(entry.getKey());
						refundIdList.add(refundId);
						map.put(refundId, entry.getValue());
					}
				}

				/**
				 * 自动List分页工具
				 */
				ListPageUtil<Refund> pageUtil = new ListPageUtil<Refund>() {
					@Override
					public Refund addObject(Long objectId) {
						// 返回对象自定义
						if (map != null && map.containsKey(objectId)) {
							Refund refund = map.get(objectId);
							if (refund != null && refund.getRefundId() != null) {
								if(!StringUtil.compareObject(refund.getRefundType(), Refund.REFUND_TYPE_CANCEL)) {
									//直接取消的订单不显示
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
									return refund;
								}
							}
						}
						return null;
					}
				};

				/**
				 * 返回自动分页结果
				 */
				ListPageVo<List<Refund>> listPageVo = pageUtil.getPageList(refundIdList, lastId, pageidx, pagesize);
				if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
					StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
					urls.append("pageidx=" + (++pageidx) + "&");
					urls.append("lastId=" + listPageVo.getLastId() + "&");
					urls.append("pagesize=" + pagesize);
					tagModel.setNextPageURL(urls.toString());
				}
				tagModel.setData(listPageVo.getDataList());
				tagModel.setTotalPage(StringUtil.nullToInteger(listPageVo.getPageMax()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		tagModel.setMsg("请求成功");
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;

	}
}
