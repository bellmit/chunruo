package com.chunruo.cache.portal.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.core.model.ProductQuestion;
import com.chunruo.core.service.ProductQuestionManager;

@Service("productQuestionByIdCacheManager")
public class ProductQuestionByIdCacheManager{
	@Autowired
	private ProductQuestionManager questionManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'question_'+#questionId")
	public ProductQuestion getSession(Long questionId){
		return this.questionManager.get(questionId);
	}

	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'question_'+#questionId")
	public ProductQuestion updateSession(Long questionId, ProductQuestion question) {
		return question;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'question_'+#questionId")
	public void removeSession(Long questionId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
}
