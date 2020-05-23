package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.chunruo.core.model.HelpQuestion;
import com.chunruo.core.service.HelpQuestionManager;
import com.chunruo.core.util.StringUtil;

@Service("helpQuestionListCacheManager")
public class HelpQuestionListCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private HelpQuestionManager helpQuestionManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'helpQuestionList'")
	public Map<String,List<HelpQuestion>> getSession(){
		Map<String,List<HelpQuestion>> map = new HashMap<String,List<HelpQuestion>>();
		List<HelpQuestion> helpQuestionList = this.helpQuestionManager.getAll();
		if(helpQuestionList != null && !helpQuestionList.isEmpty()) {
			Collections.sort(helpQuestionList,new Comparator<HelpQuestion>() {
				@Override
				public int compare(HelpQuestion o1, HelpQuestion o2) {
					Integer sort1 = StringUtil.nullToInteger(o1.getSort());
					Integer sort2 = StringUtil.nullToInteger(o2.getSort());
					return sort1.compareTo(sort2);
				}
			});
			for(HelpQuestion question : helpQuestionList) {
				String type = StringUtil.null2Str(question.getType());
				if(map.containsKey(type)) {
					map.get(type).add(question);
				}else {
					List<HelpQuestion>  list = new ArrayList<HelpQuestion>();
					list.add(question);
					map.put(type, list);
				}
			}
		}
		return map;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'helpQuestionList'")
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		List<HelpQuestion> helpQuestionList = this.helpQuestionManager.getHelpQuestionListByUpdateTime(new Date(nextLastTime));
		if(helpQuestionList != null && helpQuestionList.size() > 0){
			cacheObject.setSize(helpQuestionList.size());
			Date lastUpdateTime = null;
			for(final HelpQuestion helpQuestion : helpQuestionList){
				if(lastUpdateTime == null || lastUpdateTime.before(helpQuestion.getUpdateTime())){
					lastUpdateTime = helpQuestion.getUpdateTime();
				}
			}
			
			try{
				// 更新缓存信息
				HelpQuestionListCacheManager helpQuestionListCacheManager = Constants.ctx.getBean(HelpQuestionListCacheManager.class);
				helpQuestionListCacheManager.removeSession();
			}catch(Exception e){
				e.printStackTrace();
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}
