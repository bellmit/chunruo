package com.chunruo.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Resource;
import com.chunruo.security.repository.ResourceRepository;
import com.chunruo.security.service.ResourceManager;

@Transactional
@Component("resourceManager")
public class ResourceManagerImpl extends GenericManagerImpl<Resource, Long> implements ResourceManager{
	private ResourceRepository resourceRepository;

	@Autowired
	public ResourceManagerImpl(ResourceRepository resourceRepository) {
		super(resourceRepository);
		this.resourceRepository = resourceRepository;
	}

	@Override
	public boolean isExistName(String name, Long resourceId) {
		List<Resource> list = this.resourceRepository.getResourceByName(StringUtil.null2Str(name).toUpperCase());
		if(list != null && list.size() > 0){
			if(resourceId != null){
				for(Resource resource : list){
					if(!StringUtil.compareObject(resource.getResourceId(), resourceId)){
						return true;
					}
				}
			}else{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isExistLinkPath(String linkPath, Long resourceId) {
		List<Resource> list = this.resourceRepository.getResourceByLinkPath(linkPath);
		if(list != null && list.size() > 0){
			if(resourceId != null){
				for(Resource resource : list){
					if(!StringUtil.compareObject(resource.getResourceId(), resourceId)){
						return true;
					}
				}
			}else{
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateEnable(List<Long> resourceIdList, boolean isEnable) {
		if(resourceIdList != null && resourceIdList.size() > 0){
			this.resourceRepository.updateEnable(resourceIdList, isEnable);
		}
	}
}
