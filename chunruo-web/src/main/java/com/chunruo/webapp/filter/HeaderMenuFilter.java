package com.chunruo.webapp.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chunruo.core.Constants;
import com.chunruo.security.SecurityConstants;
import com.chunruo.security.model.Menu;
import com.chunruo.security.model.User;
import com.chunruo.security.vo.TreeNode;

/**
 * 检验验证码
 * @author Administrator
 *
 */
@Component("headerMenuFilter")
public class HeaderMenuFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//获得用户的信息
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null 
				&& auth.getPrincipal() != null
				&& auth.getPrincipal() instanceof User){
			// 检查用户是否配合权限
			User user = (User) auth.getPrincipal();
			if(user.getMenuIdSet() != null && user.getMenuIdSet().size() > 0){
				List<Menu> list = new ArrayList<Menu> ();
				for(Long menuId : user.getMenuIdSet()){
					list.addAll(this.getRecursionMenu(menuId));
				}
				
				// 过滤重复的菜单
				List<Menu> menuList = new ArrayList<Menu> ();
				if(list != null && list.size() > 0){
					Set<Long> menuIdSet = new HashSet<Long> ();
					for(Menu menu : list){
						if(!menuIdSet.contains(menu.getMenuId())){
							menuList.add(menu);
							menuIdSet.add(menu.getMenuId());
						}
					}
					
					// 根据权限动态菜单
					Map<Long, List<Menu>> menuListMap = new HashMap<Long, List<Menu>> ();
					for (Menu menu : menuList) {
						if (menu.getParentMenu() != null && menu.getParentMenu().getMenuId() != null) {
							Long menuParentId = menu.getParentMenu().getMenuId();
							if (menuListMap.containsKey(menuParentId)) {
								menuListMap.get(menuParentId).add(menu);
							} else {
								List<Menu> l = new ArrayList<Menu>();
								l.add(menu);
								menuListMap.put(menuParentId, l);
							}
						}
					}
					
					TreeNode treeNode = new TreeNode();
					SecurityConstants.initMenuTreeNode(treeNode, Constants.TOP_DEFUALT_ID, menuListMap);
					request.setAttribute("headerMenuTreeMaps", treeNode.getChildrenNode());
				}
			}
		}
		filterChain.doFilter(request, response); 
	}
	
	/**
	 * 递归遍历菜单
	 * @param menuId
	 * @return
	 */
	public List<Menu> getRecursionMenu(Long menuId){
		List<Menu> list = new ArrayList<Menu> ();
		if(SecurityConstants.MENU_MAP.containsKey(menuId)){
			Menu menu = SecurityConstants.MENU_MAP.get(menuId);
			if(menu != null && menu.getMenuId() != null){
				list.add(menu);
				
				// 查询父类对象
				if(menu.getParentMenu() != null && menu.getParentMenu().getMenuId() != null){
					list.addAll(this.getRecursionMenu(menu.getParentMenu().getMenuId()));
				}
			}
		}
		return list;
	}
}
