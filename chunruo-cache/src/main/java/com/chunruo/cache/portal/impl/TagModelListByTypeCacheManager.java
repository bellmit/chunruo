package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
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
import com.chunruo.core.model.TagModel;
import com.chunruo.core.service.TagModelManager;
import com.chunruo.core.util.StringUtil;

@Service("tagModelListByTypeCacheManager")
public class TagModelListByTypeCacheManager extends BaseCacheManagerImpl {
	@Autowired
	private TagModelManager tagModelManager;
	
	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'tagModelList_'+#objectId+'_'+#tagType")
	public List<String> getSession(Long objectId, Integer tagType){
		List<String> tagModelNameList = new ArrayList<String> ();
		List<TagModel> list = this.tagModelManager.getTagModelListByObjectId(objectId, tagType);
		if(list != null && list.size() > 0){
			for(TagModel tagModel : list){
				tagModelNameList.add(tagModel.getName());
			}
		}
		return tagModelNameList;
	}
	
	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'tagModelList_'+#objectId+'_'+#tagType")
	public void removeSession(Long objectId, Integer tagType) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		TagModelListByTypeCacheManager tagModelListByTypeCacheManager = Constants.ctx.getBean(TagModelListByTypeCacheManager.class);
		List<TagModel> tagModelList = tagModelManager.getTagModelListByUpdateTime(new Date(nextLastTime));
		if(tagModelList != null && tagModelList.size() > 0){
			cacheObject.setSize(tagModelList.size());
			Date lastUpdateTime = null;
			Set<Long> brandIdSet = new HashSet<Long> ();
			Set<Long> cateGroryIdSet = new HashSet<Long> ();
			for(final TagModel tagModel : tagModelList){
				if(StringUtil.compareObject(TagModel.BRAND_TAG_TYPE, tagModel.getTagType())){
					//商品品牌标签
					brandIdSet.add(tagModel.getObjectId());
				}else if(StringUtil.compareObject(TagModel.CATEGORY_TAG_TYPE, tagModel.getTagType())){
					//商品分类标签
					cateGroryIdSet.add(tagModel.getObjectId());
				}
			}
			
			// 更新品牌标签
			if(brandIdSet != null && brandIdSet.size() > 0){
				for(Long brandId : brandIdSet){
					try{
						tagModelListByTypeCacheManager.removeSession(brandId, TagModel.BRAND_TAG_TYPE);
					}catch(Exception e){
						e.printStackTrace();
						continue;
					}
				}
			}
			
			// 更新分类标签
			if(cateGroryIdSet != null && cateGroryIdSet.size() > 0){
				for(Long cateGroryId : cateGroryIdSet){
					try{
						tagModelListByTypeCacheManager.removeSession(cateGroryId, TagModel.CATEGORY_TAG_TYPE);
					}catch(Exception e){
						e.printStackTrace();
						continue;
					}
				}
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
}

