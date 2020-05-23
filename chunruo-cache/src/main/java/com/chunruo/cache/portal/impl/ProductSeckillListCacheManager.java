package com.chunruo.cache.portal.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.service.ProductSeckillManager;
import com.chunruo.core.util.StringUtil;

/**
 * 秒杀场次列表
 * @author chunruo
 *
 */
@Service("productSeckillListCacheManager")
public class ProductSeckillListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductSeckillManager productSeckillManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSeckillList'")
	public Map<String, ProductSeckill> getSession(){
		Map<String, ProductSeckill> productSeckillMap = new HashMap<String, ProductSeckill> ();
		try{
			List<ProductSeckill> list = this.productSeckillManager.getProductSeckillListByStatus(true);
			if(list != null && list.size() > 0){
				for(ProductSeckill productSeckill : list){
					productSeckillMap.put(StringUtil.null2Str(productSeckill.getSeckillId()), productSeckill);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return productSeckillMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productSeckillList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		ProductSeckillListCacheManager productSeckillListCacheManager = Constants.ctx.getBean(ProductSeckillListCacheManager.class);
		List<ProductSeckill> productSeckillList = productSeckillManager.getProductSeckillByUpdateTime(new Date(nextLastTime));
		if(productSeckillList != null && productSeckillList.size() > 0){
			cacheObject.setSize(productSeckillList.size());
			Date lastUpdateTime = null;
			for(final ProductSeckill productSeckill : productSeckillList){
				if(lastUpdateTime == null || lastUpdateTime.before(productSeckill.getUpdateTime())){
					lastUpdateTime = productSeckill.getUpdateTime();
				}
			}
			
			try{
				// 更新渠道缓存列表
				productSeckillListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
