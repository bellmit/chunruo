package com.chunruo.cache.portal.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.chunruo.core.model.PageView;
import com.chunruo.core.model.VisvitCount;
import com.chunruo.core.service.PageViewManager;
import com.chunruo.core.service.VisvitCountManager;
import com.chunruo.core.util.StringUtil;

@SuppressWarnings("rawtypes")
@Service("pageViewCacheManager")
public class PageViewCacheManager {
	private final static String PAGE_VIEW_NUMBER = "pageViewNumber";
	@Autowired
	public RedisTemplate redisTemplate;
	@Autowired
	public PageViewManager pageViewManager;
	@Autowired
	public VisvitCountManager visvitCountManager;
	
	@SuppressWarnings("unchecked")
	public Long addSession() {
		Long sessionNumber = StringUtil.nullToLong(this.redisTemplate.opsForValue().get(PageViewCacheManager.PAGE_VIEW_NUMBER));
		if (StringUtil.compareObject(sessionNumber, 0)) {
			
			List<PageView> viewList = this.pageViewManager.getAll();
			if (viewList != null && viewList.size() > 0) {
				Collections.sort(viewList, new Comparator<PageView>(){
					public int compare(PageView obj1, PageView obj2){
						Long number1 = StringUtil.nullToLong(obj1.getNumber());
						Long number2 = StringUtil.nullToLong(obj2.getNumber());
						return -number1.compareTo(number2);
					}
				}); 
				
				//得到最大的
				PageView pageView = viewList.get(0);
				if (pageView != null && pageView.getViewId() != null) {
					Long number = StringUtil.nullToLong(pageView.getNumber());

					// 设置缓存初始值
					this.redisTemplate.opsForValue().set(PageViewCacheManager.PAGE_VIEW_NUMBER, number);
				}
			}else {
				//系统第一次启动读取以前的浏览量，此快仅使用一次
				List<VisvitCount> visvitCountList = this.visvitCountManager.getAll();
				if(visvitCountList != null) {
					this.redisTemplate.opsForValue().set(PageViewCacheManager.PAGE_VIEW_NUMBER, StringUtil.nullToLong(visvitCountList.size()));
				}
			}
		}
		// 自增
		Long count = this.redisTemplate.opsForValue().increment(PageViewCacheManager.PAGE_VIEW_NUMBER, 1);
		return count;
	}
	
	public Long getSession(){
		return StringUtil.nullToLong(this.redisTemplate.opsForValue().get(PageViewCacheManager.PAGE_VIEW_NUMBER));
	}
}





