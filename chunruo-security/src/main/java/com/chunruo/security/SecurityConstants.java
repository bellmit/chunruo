package com.chunruo.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunruo.core.Constants;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Menu;
import com.chunruo.security.service.MenuManager;
import com.chunruo.security.vo.TreeNode;

public class SecurityConstants {
	public static final Long MENU_DEFULATE_ID = -1L;
	public static TreeNode MENU_TREE_NODE = new TreeNode ();
	public static Map<Long, Menu> MENU_MAP = new HashMap<Long, Menu> ();
	public static Map<Long, List<Menu>> MENU_LIST_MAP = new HashMap<Long, List<Menu>> ();

	/**
	 * 菜单列表缓存
	 */
	public static void initMenuMap() {
		MenuManager menuManager = Constants.ctx.getBean(MenuManager.class);
		List<Menu> menuList = menuManager.getAll();
		if (menuList != null && menuList.size() > 0) {
			SecurityConstants.MENU_MAP.clear();
			SecurityConstants.MENU_LIST_MAP.clear();
			SecurityConstants.MENU_TREE_NODE = new TreeNode();
			for (Menu menu : menuList) {
				if(!StringUtil.nullToBoolean(menu.getStatus())) {
					continue;
				}
				SecurityConstants.MENU_MAP.put(menu.getMenuId(), menu);
				if (menu.getParentMenu() != null && menu.getParentMenu().getMenuId() != null) {
					Long menuParentId = menu.getParentMenu().getMenuId();
					if (SecurityConstants.MENU_LIST_MAP.containsKey(menuParentId)) {
						SecurityConstants.MENU_LIST_MAP.get(menuParentId).add(menu);
					} else {
						List<Menu> list = new ArrayList<Menu>();
						list.add(menu);
						SecurityConstants.MENU_LIST_MAP.put(menuParentId, list);
					}
				}
			}
			SecurityConstants.initMenuTreeNode(SecurityConstants.MENU_TREE_NODE, Constants.TOP_DEFUALT_ID, SecurityConstants.MENU_LIST_MAP);
		}
	}

	/**
	 * 枚举树遍历
	 * 
	 * @param treeNode
	 * @param menuParentId
	 * @param menuListMap
	 */
	public static void initMenuTreeNode(TreeNode treeNode, Long menuParentId, Map<Long, List<Menu>> menuListMap) {
		SecurityConstants.initMenuTreeNode(treeNode, menuParentId, menuListMap, true);
	}

	/**
	 * 枚举树遍历
	 * 
	 * @param treeNode
	 * @param menuParentId
	 * @param menuListMap
	 */
	public static void initMenuTreeNode(TreeNode treeNode, Long menuParentId, Map<Long, List<Menu>> menuListMap,
			boolean isDaymic) {
		if (menuListMap != null && menuListMap.containsKey(menuParentId)) {
			List<Menu> menuList = menuListMap.get(menuParentId);
			Collections.sort(menuList, new Comparator<Menu>() {
				public int compare(Menu o1, Menu o2) {
					return (o2.getSequence() < o1.getSequence()) ? 1 : -1;
				}
			});

			for (Menu menu : menuList) {
				TreeNode menuTreeNode = new TreeNode(menu);
				if (menuListMap.containsKey(menu.getMenuId())) {
					initMenuTreeNode(menuTreeNode, menu.getMenuId(), menuListMap, isDaymic);
				}
				treeNode.getChildren().put(menu.getMenuId(), menuTreeNode);
			}
		}
	}
}
