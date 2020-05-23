package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Sign;
import com.chunruo.core.service.SignManager;
import com.chunruo.core.util.BaseThreadPool;

@Service("signByUserIdCacheManager")
public class SignByUserIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private SignManager signManager;

	@Cacheable(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'sign_'+#userId")
	public Sign getSession(Long userId) {
		Sign sign = this.signManager.getSignByUserId(userId);
		if (sign != null && sign.getSignId() != null) {
			return sign;
		}
		return null;
	}

	@CacheEvict(value = "sessionEhRedisCache", cacheManager = "sessionEhRedisCacheManager", key = "'sign_'+#userId")
	public void removeSession(Long userId) {
		// 如果过期后要做特殊处理，可在此实现
		// log.info("removeSession userId:" + userId + ",userToken:" +
		// userToken);
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();

		final SignByUserIdCacheManager signByUserIdCacheManager = Constants.ctx.getBean(SignByUserIdCacheManager.class);
		List<Sign> signList = this.signManager.getSignListByUpdateTime(new Date(nextLastTime));
		if (signList != null && signList.size() > 0) {
			cacheObject.setSize(signList.size());
			Date lastUpdateTime = null;
			Set<Long> userIdSet = new HashSet<Long>();
			for (final Sign sign : signList) {
				if (lastUpdateTime == null || lastUpdateTime.before(sign.getUpdateTime())) {
					lastUpdateTime = sign.getUpdateTime();
				}

				if (!userIdSet.contains(sign.getUserId())) {
					userIdSet.add(sign.getUserId());
				}
			}

			if (userIdSet != null && userIdSet.size() > 0) {
				for (final Long userId : userIdSet) {
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable() {
						@Override
						public void run() {
							try {
								// 更新缓存
								signByUserIdCacheManager.removeSession(userId);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
