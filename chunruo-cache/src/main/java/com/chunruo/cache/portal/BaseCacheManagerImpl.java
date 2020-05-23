package com.chunruo.cache.portal;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.chunruo.cache.portal.impl.DateCacheManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@SuppressWarnings("rawtypes")
public abstract class BaseCacheManagerImpl implements BaseCacheManager{
	public static Log log = LogFactory.getLog(BaseCacheManagerImpl.class);
	@Autowired
	public DateCacheManager dateCacheManager;	
	
	public abstract CacheObject run(Long nextLastTime);

	@Override
	public int execute()throws Exception{
		int size = 0;
		try{
			String cacheName = this.getCacheName();
			//设置最后更新时间
			Date currentDate = DateUtil.getCurrentDate();
			Long sessionNextLastTime = StringUtil.nullToLong(dateCacheManager.getSession(cacheName));
			if(StringUtil.compareObject(sessionNextLastTime, 0)){
				sessionNextLastTime = currentDate.getTime();
			}
			
			// 从缓存中去掉上次缓存时间
			CacheObject cacheObject = this.run(sessionNextLastTime + 1L);
			if(cacheObject != null){
				// 更新数量
				size = StringUtil.nullToInteger(cacheObject.getSize());
				
				// 标记上一次更新时间
				if(cacheObject.getLastUpdateTime() != null && cacheObject.getLastUpdateTime().before(currentDate)){
					try{
						this.dateCacheManager.updateSession(cacheName, cacheObject.getLastUpdateTime().getTime());
					}catch(Exception e){
						e.printStackTrace();
						log.debug(e.getMessage());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return size;
	}
	
	@Override
	public String getCacheName(){
		return this.getClass().getSimpleName();
	}
}
