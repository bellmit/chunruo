package com.chunruo.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Group;
import com.chunruo.security.repository.GroupRepository;
import com.chunruo.security.service.GroupManager;

@Transactional
@Component("groupManager")
public class GroupManagerImpl extends GenericManagerImpl<Group, Long> implements GroupManager{
	private GroupRepository groupRepository;

	@Autowired
	public GroupManagerImpl(GroupRepository groupRepository) {
		super(groupRepository);
		this.groupRepository = groupRepository;
	}

	@Override
	public boolean isExistName(String name, Long groupId) {
		List<Group> list = this.groupRepository.getGroupByName(StringUtil.null2Str(name).toUpperCase());
		if(list != null && list.size() > 0){
			if(groupId != null){
				for(Group group : list){
					if(!StringUtil.compareObject(group.getGroupId(), groupId)){
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
	public void updateEnable(List<Long> groupIdList, boolean isEnable) {
		if(groupIdList != null && groupIdList.size() > 0){
			this.groupRepository.updateEnable(groupIdList, isEnable);
		}
	}
}
