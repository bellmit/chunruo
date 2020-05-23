package com.chunruo.security.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.security.model.Menu;
import com.chunruo.security.repository.MenuRepository;
import com.chunruo.security.service.MenuManager;

@Transactional
@Component("menuManager")
public class MenuManagerImpl extends GenericManagerImpl<Menu, Long> implements MenuManager{
	private MenuRepository menuRepository;

	@Autowired
	public MenuManagerImpl(MenuRepository menuRepository) {
		super(menuRepository);
		this.menuRepository = menuRepository;
	}

	@Override
	public List<Menu> getMenuListByParentId(Long menuId) {
		return this.menuRepository.getMenuListByParentId(menuId);
	}
	
}
