package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductAnswer;
import com.chunruo.core.service.ProductAnswerManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("productAnswerListByQuestionIdCacheManager")
public class ProductAnswerListByQuestionIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductAnswerManager answerManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'answerList_'+#questionId")
	public Map<String, ProductAnswer> getSession(Long questionId){
		Map<String, ProductAnswer> answerIdMap = new HashMap<String, ProductAnswer> ();
		List<ProductAnswer> answerList = this.answerManager.getAnswerListByQuestionId(questionId);
		if(answerList != null && answerList.size() > 0){
			for(ProductAnswer answer : answerList){
				if (answer.getCreateTime() != null){
					answerIdMap.put(StringUtil.null2Str(answer.getAnswerId()), answer);
				}
			}
		}
		return answerIdMap;
	}

	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'answerList_'+#questionId")
	public Map<String, ProductAnswer> updateSession(Long questionId, ProductAnswer answer) {
		Map<String, ProductAnswer> questionMap = this.getSession(questionId);
		if(questionMap == null || questionMap.size() <= 0){
			questionMap = new HashMap<String, ProductAnswer> ();
		}
		
		if (answer != null 
				&& answer.getAnswerId() != null 
				&& answer.getCreateTime() != null){
			questionMap.put(StringUtil.null2Str(answer.getAnswerId()), answer);
		}
		return questionMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'answerList_'+#questionId")
	public void removeSession(Long questionId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final ProductAnswerListByQuestionIdCacheManager productAnswerListByQuestionIdCacheManager = Constants.ctx.getBean(ProductAnswerListByQuestionIdCacheManager.class);
		List<ProductAnswer> answerList = answerManager.getAnswerListByUpdateTime(new Date(nextLastTime));
		if(answerList != null && answerList.size() > 0){
			cacheObject.setSize(answerList.size());
			Date lastUpdateTime = null;
			List<Long> questionIdList = new ArrayList<Long> ();
			for(ProductAnswer answer : answerList){
				if(lastUpdateTime == null || lastUpdateTime.before(answer.getUpdateTime())){
					lastUpdateTime = answer.getUpdateTime();
				}
				
				// 收集商品问题统一规整避免重复删除
				if(questionIdList == null || !questionIdList.contains(answer.getQuestionId())){
					questionIdList.add(answer.getQuestionId());
				}
			}
			
			// 统一规整清除缓存信息
			if(questionIdList != null && questionIdList.size() > 0){
				for(final Long questionId : questionIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新回答缓存
								productAnswerListByQuestionIdCacheManager.removeSession(questionId);
							}catch(Exception e){
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
