package com.chunruo.portal.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chunruo.cache.portal.impl.OrderListByStoreIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.OrderUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 订单列表 全部店铺订单显示
 * @author chunruo
 *
 */
public class OrderListTag extends BaseTag {

	public TagModel<List<Order>> getData(Object pageidx_1, Object pagesize_1, Object status_1, Object lastId_1,Object keyword_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Integer status = StringUtil.nullToInteger(status_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
		String keyword = StringUtil.null2Str(keyword_1).toLowerCase();
		keyword = StringUtil.decode(keyword);

		Long startTime = System.currentTimeMillis();
		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<Order>> tagModel = new TagModel<List<Order>>();
		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			// 代理商才有订单信息显示
			OrderListByStoreIdCacheManager orderListByStoreIdCacheManager = Constants.ctx.getBean(OrderListByStoreIdCacheManager.class);
			List<Order> orderList = orderListByStoreIdCacheManager.getSession(userInfo.getUserId());
			if (orderList != null && orderList.size() > 0) {
				ListPageVo<List<Order>> listPageVo = OrderUtil.getOrderListPageVO(orderList, status, pageidx, pagesize, lastId, userInfo, keyword);
				if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
					StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
					urls.append("pageidx=" + (++pageidx) + "&");
					urls.append("status=" + status + "&");
					urls.append("lastId=" + listPageVo.getLastId() + "&");
					urls.append("keyword=" + keyword + "&");
					urls.append("pagesize=" + pagesize);
					tagModel.setNextPageURL(urls.toString());
				}
				tagModel.setData(listPageVo.getDataList());
				tagModel.setTotal(StringUtil.nullToLong(listPageVo.getCount()));
				tagModel.setTotalPage(StringUtil.nullToInteger(listPageVo.getPageMax()));
			}
			
			Map<String,Object> dataMap = new HashMap<String, Object>();
			dataMap.put("cancelReasonList", OrderDetailTag.getOrderCancelReasonList());
			tagModel.setDataMap(dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("订单列表耗时："+(endTime - startTime) / 1000+"s");

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}
