package com.chunruo.cache.portal.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.Refund;
import com.chunruo.core.service.RefundManager;
import com.chunruo.core.util.StringUtil;

@Service("refundListByStoreIdCacheManager")
public class RefundListByStoreIdCacheManager  {
	@Autowired
	private RefundManager refundManager;

	@Cacheable(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'refundListByStoreId_'+#storeId")
	public Map<String, Refund> getSession(Long storeId) {
		Map<String, Refund> refundMap = new HashMap<String, Refund> ();
		List<Refund> refundList = this.refundManager.getRefundListByStoreId(storeId, true);
		if (refundList != null && refundList.size() > 0) {
			for (Refund refund : refundList)
				refundMap.put(StringUtil.nullToString(refund.getRefundId()), refund);
		}
		return refundMap;
	}

	@CacheEvict(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'refundListByStoreId_'+#storeId")
	public void removeSession(Long storeId) {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}
}
