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
import com.chunruo.core.model.ProductQuestion;
import com.chunruo.core.service.ProductQuestionManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.StringUtil;

@Service("productQuestionListByProductIdCacheManager")
public class ProductQuestionListByProductIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductQuestionManager questionManager;
	@Autowired
	private ProductQuestionByIdCacheManager productQuestionByIdCacheManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'questionList_'+#productId")
	public Map<String, ProductQuestion> getSession(Long productId){
		Map<String, ProductQuestion> questionIdMap = new HashMap<String, ProductQuestion> ();
		List<ProductQuestion> questionList = this.questionManager.getQuestionListByProductId(productId);
		if(questionList != null && questionList.size() > 0){
			for(ProductQuestion question : questionList){
				this.productQuestionByIdCacheManager.updateSession(question.getQuestionId(), question);
				if (question.getCreateTime() != null){
					questionIdMap.put(StringUtil.null2Str(question.getQuestionId()), question);
				}
			}
		}
		return questionIdMap;
	}

	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'questionList_'+#productId")
	public Map<String,ProductQuestion> updateSession(Long productId, ProductQuestion question) {
		Map<String,ProductQuestion> questionMap = this.getSession(productId);
		if(questionMap == null || questionMap.size() <= 0){
			questionMap = new HashMap<String,ProductQuestion> ();
		}
		
		if (question != null 
				&& question.getQuestionId() != null 
				&& question.getCreateTime() != null){
			questionMap.put(StringUtil.null2Str(question.getQuestionId()), question);
		}
		return questionMap;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'questionList_'+#productId")
	public void removeSession(Long productId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		final ProductQuestionListByProductIdCacheManager productQuestionListByProductIdCacheManager = Constants.ctx.getBean(ProductQuestionListByProductIdCacheManager.class);
		List<ProductQuestion> questionList = this.questionManager.getQuestionListByUpdateTime(new Date(nextLastTime));
		if(questionList != null && questionList.size() > 0){
			cacheObject.setSize(questionList.size());
			Date lastUpdateTime = null;
			List<Long> productIdList = new ArrayList<Long> ();
			for(ProductQuestion question : questionList){
				if(lastUpdateTime == null || lastUpdateTime.before(question.getUpdateTime())){
					lastUpdateTime = question.getUpdateTime();
				}
				
				// 收集批发市场ID统一规整避免重复删除
				if(productIdList == null || !productIdList.contains(question.getProductId())){
					productIdList.add(question.getProductId());
				}
			}
			
			// 统一规整清除缓存信息
			if(productIdList != null && productIdList.size() > 0){
				for(final Long productId : productIdList){
					BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
						@Override
						public void run() {
							try{
								// 更新问题缓存
								productQuestionListByProductIdCacheManager.removeSession(productId);
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
