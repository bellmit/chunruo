package com.chunruo.security.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.security.model.Menu;

public interface MenuManager extends GenericManager<Menu, Long>{

	public List<Menu> getMenuListByParentId(Long menuId);
}
