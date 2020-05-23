package com.chunruo.portal.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chunruo.cache.portal.BaseCacheManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

/**
 * 自动更新对象缓存
 * 每3分钟自动启动
 * @author chunruo
 */
@Component
public class AutoCacheCleanTask {
	private static Log log = LogFactory.getLog(AutoCacheCleanTask.class);
	public static AtomicReference<Map<String, Boolean>> atomicReference = new AtomicReference<Map<String, Boolean>> ();
	@Autowired
	private List<BaseCacheManager> list;
	
	@Scheduled(cron="0 0/2 * * * *")
	public void execute(){
		log.info("AutoCacheCleanTask ........");
		for(final BaseCacheManager cache : list){
			BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
				@Override
				public void run() {
					boolean isExecuteExist = false;
					String cacheName = cache.getCacheName();
					try{
						// 获取缓存可执行状态
						boolean status = AutoCacheCleanTask.getCacheStatus(cacheName, true);
						if(status){
							isExecuteExist = true;
							long start = System.currentTimeMillis();
							int size = cache.execute();
							AutoCacheCleanTask.getCacheStatus(cacheName, false);
							long end = System.currentTimeMillis();
							log.info("AutoCacheCleanTask  === " + String.format("[name=%s,size=%s]耗时%s秒", cacheName, size, (end - start)/1000));
						}else{
							log.info("AutoCacheCleanTask  === " + String.format("[name=%s]获取执行锁失败", cacheName));
						}
					}catch(Exception e){
						e.printStackTrace();
						// 异常执行恢复执行状态
						if(isExecuteExist){
							AutoCacheCleanTask.getCacheStatus(cacheName, false);
						}
						log.info("AutoCacheCleanTask  === " + String.format("[name=%s]异常>>%s", cacheName, StringUtil.null2Str(e.getMessage())));
					}
				}
			});
		}
		log.info("AutoCacheCleanTask  === " + String.format("[size=%s]", list.size()));
	}
	
	/**
	 * 获取value=true可执行任务
	 * @param cacheName
	 * @return
	 */
	public synchronized static boolean getCacheStatus(String cacheName, boolean isGetValue){
		Map<String, Boolean> map = atomicReference.get();
		if(map == null || map.size() <= 0){
			map = new HashMap<String, Boolean> ();
		}
		
		if(StringUtil.nullToBoolean(isGetValue)){
			Boolean vaule = map.get(cacheName);
			if(vaule == null || StringUtil.nullToBoolean(vaule)){ 
				map.put(cacheName, false); 
				atomicReference.set(map);
				return true;
			}
		}else{
			map.put(cacheName, true);
			atomicReference.set(map);
		}
		return false;
	}
}
