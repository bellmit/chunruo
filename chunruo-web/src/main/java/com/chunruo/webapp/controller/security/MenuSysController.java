package com.chunruo.webapp.controller.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.chunruo.core.Constants;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.SecurityConstants;
import com.chunruo.security.model.Menu;
import com.chunruo.security.service.MenuManager;
import com.chunruo.security.vo.TreeNode;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/menuSys/")
public class MenuSysController extends BaseController {
	@Autowired
	private MenuManager menuManager;

	@RequestMapping(value="/queryMenuNodes")
	public ModelAndView queryMenuNodes(final HttpServletRequest request){
		Boolean isResRequest = StringUtil.nullToBoolean(request.getParameter("isResRequest"));
		TreeNode treeNode = new TreeNode ();
		List<Menu> menuList = this.menuManager.getAll();
		if(menuList != null && menuList.size() > 0){
			Map<Long, Menu> menuMap = new HashMap<Long, Menu> ();
			Map<Long, List<Menu>> menuListMap = new HashMap<Long, List<Menu>> ();
			for(Menu menu : menuList){
				menuMap.put(menu.getMenuId(), menu);
				if(menu.getParentMenu() != null && menu.getParentMenu().getMenuId() != null){
					Long menuParentId = menu.getParentMenu().getMenuId();
					if(menuListMap.containsKey(menuParentId)){
						menuListMap.get(menuParentId).add(menu);
					}else{
						List<Menu> list = new ArrayList<Menu> ();
						list.add(menu);
						menuListMap.put(menuParentId, list);
					}
				}
			}
			
			SecurityConstants.initMenuTreeNode(treeNode, Constants.TOP_DEFUALT_ID, menuListMap, false);
			if(treeNode != null && treeNode.getChildrenNode() != null){
				for(TreeNode childrenNode : treeNode.getChildrenNode()){
					childrenNode.setNamePath(childrenNode.getName());
					if(childrenNode.getChildrenNode() != null && childrenNode.getChildrenNode() != null){
						TreeNode.setNamePaths(childrenNode.getChildrenNode(), childrenNode.getNamePath());
					}
				}
			}
		}
		
		// 访问资源挂载菜单节点
		List<TreeNode> nodeList = treeNode.getChildrenNode();
		if(StringUtil.nullToBoolean(isResRequest)
				&& nodeList != null
				&& nodeList.size() > 0){
			TreeNode node = new TreeNode ();
			node.setMenuId(SecurityConstants.MENU_DEFULATE_ID);
			node.setName("公共访问节点");
			node.setNamePath(node.getName());
			nodeList.add(node);
		}

		Model model = new ExtendedModelMap();
		model.addAttribute(Constants.MENU_TREE_MAPS, nodeList);
		return new ModelAndView("nodes/menuNodes", model.asMap());
	}

	@RequestMapping(value="/getMenuById")
	public @ResponseBody Map<String, Object> getMenuById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long menuId = StringUtil.nullToLong(request.getParameter("menuId"));
		Menu menu = new Menu ();;
		try{
			menu = this.menuManager.get(menuId);
			if(menu != null && menu.getMenuId() != null){
				menu.setParentMenu(null);
				menu.setStatus(StringUtil.nullToBoolean(menu.getStatus()));
			}
		}catch(Exception e){
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("menu", menu);
		return resultMap;
	}

	@RequestMapping(value="/saveMenu")
	public @ResponseBody Map<String, Object> saveMenu(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long menuId = StringUtil.nullToLong(request.getParameter("menuId"));
		String ctrl = StringUtil.null2Str(request.getParameter("ctrl"));
		String name = StringUtil.null2Str(request.getParameter("name"));
		String icon = StringUtil.null2Str(request.getParameter("icon"));
		Integer sequence = StringUtil.nullToInteger(request.getParameter("sequence"));
		Long parentId = StringUtil.nullToLong(request.getParameter("parentId"));
		Boolean status = StringUtil.nullToBoolean(request.getParameter("status"));
		String desc = StringUtil.null2Str(request.getParameter("desc"));

		Menu menu = new Menu ();
		try{
			// 检查是否新增内容
			boolean isNewMenu = true;
			Menu tmpMenu = this.menuManager.get(menuId);
			if(tmpMenu != null && tmpMenu.getMenuId() != null){
				isNewMenu = false;
				menu.setMenuId(tmpMenu.getMenuId());
				menu.setParentMenu(tmpMenu.getParentMenu());
			}

			// 新增内容父类关系
			if(isNewMenu){
				if(StringUtil.compareObject(parentId, 0)){
					Menu parentMenu = SecurityConstants.MENU_MAP.get(Constants.TOP_DEFUALT_ID);
					if(parentMenu != null && parentMenu.getMenuId() != null){
						menu.setParentMenu(parentMenu);
					}
				}else{
					Menu parentMenu = this.menuManager.get(parentId);
					if(parentMenu != null && parentMenu.getMenuId() != null){
						menu.setParentMenu(parentMenu);
					}
				}
			}

			menu.setCtrl(ctrl);
			menu.setName(name);
			menu.setIcon(icon);
			menu.setSequence(sequence);
			menu.setParentId(parentId);
			menu.setStatus(status);
			menu.setDesc(desc);
			menu = this.menuManager.save(menu);

			try{
				SecurityConstants.initMenuMap();
			}catch(Exception e){
				e.printStackTrace();
			}
			resultMap.put("menu", menu);
			resultMap.put("success", true);
			resultMap.put("error", false);
			resultMap.put("message", this.getText("save.success"));
			return resultMap;
		}catch(Exception e){
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("error", true);
		resultMap.put("message", this.getText("save.failure"));
		return resultMap;
	}

	@RequestMapping(value="/deleteMenuById")
	public @ResponseBody Map<String, Object> deleteMenuById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long menuId = StringUtil.nullToLong(request.getParameter("menuId"));
		try{
			List<Menu> menuList = (List<Menu>) this.menuManager.getMenuListByParentId(menuId);
			if(menuList != null && menuList.size() > 0){
				resultMap.put("success", false);
				resultMap.put("message", this.getText("menu.child.exits.error"));
				return resultMap;
			}

			this.menuManager.remove(menuId);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("delete.success"));
			return resultMap;
		}catch(Exception e){
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", this.getText("delete.failure"));
		return resultMap;
	}
}
