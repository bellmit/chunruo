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
import com.chunruo.core.model.ProductTask;
import com.chunruo.core.service.ProductTaskManager;
import com.chunruo.core.util.StringUtil;

@Service("productTaskListCacheManager")
public class ProductTaskListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private ProductTaskManager productTaskManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productTaskList'")
	public Map<String,ProductTask> getSession(){
		Map<String,ProductTask> taskMap = new HashMap<String,ProductTask>();
		List<ProductTask> taskList = this.productTaskManager.getAll();
		if(taskList != null && taskList.size() > 0) {
			for(ProductTask task : taskList) {
				taskMap.put(StringUtil.null2Str(task.getTaskId()), task);
			}
		}
		return taskMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productTaskList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<ProductTask> productTaskList = this.productTaskManager.getProductTaskListByUpdateTime(new Date(nextLastTime));
		if(productTaskList != null && productTaskList.size() > 0){
			cacheObject.setSize(productTaskList.size());
			Date lastUpdateTime = null;
			for(final ProductTask productTask : productTaskList){
				if(lastUpdateTime == null || lastUpdateTime.before(productTask.getUpdateTime())){
					lastUpdateTime = productTask.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				ProductTaskListCacheManager productTaskListCacheManager = Constants.ctx.getBean(ProductTaskListCacheManager.class);
				productTaskListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
