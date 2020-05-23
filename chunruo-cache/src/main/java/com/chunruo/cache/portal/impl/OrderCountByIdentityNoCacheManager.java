package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.service.OrderManager;
import com.chunruo.core.util.StringUtil;

@Service("orderCountByIdentityNoCacheManager")
public class OrderCountByIdentityNoCacheManager {
	@Autowired
	private OrderManager orderManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderCountByIdentityNo_'+#identityNo")
	public int getSession(String identityNo){
		List<Integer> productTypeList = new ArrayList<Integer>();
		productTypeList.add(GoodsType.GOODS_TYPE_CROSS);
		productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);
		long count = this.orderManager.countByIdentityNo(identityNo, productTypeList);
		return StringUtil.nullToInteger(count);
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'orderCountByIdentityNo_'+#identityNo")
	public void removeSession(String identityNo) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
