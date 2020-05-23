package com.chunruo.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.security.model.Menu;

/**
 * 系统菜单
 * @author chunruo
 */
@Repository("menuRepository")
public interface MenuRepository extends GenericRepository<Menu, Long> {

	@Query("from Menu where parentMenu.menuId =:menuId")
	public List<Menu> getMenuListByParentId(@Param("menuId")Long menuId);
}
