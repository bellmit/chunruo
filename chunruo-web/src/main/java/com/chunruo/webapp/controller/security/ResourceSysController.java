package com.chunruo.webapp.controller.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.chunruo.core.Constants;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.SecurityConstants;
import com.chunruo.security.model.Menu;
import com.chunruo.security.model.Resource;
import com.chunruo.security.model.Role;
import com.chunruo.security.service.MenuManager;
import com.chunruo.security.service.ResourceManager;
import com.chunruo.security.service.RoleManager;
import com.chunruo.security.vo.TreeNode;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/resourceSys/")
public class ResourceSysController extends BaseController {
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private ResourceManager resourceManager;

	@RequestMapping(value="/queryResourceMenuNodes")
	public ModelAndView queryResourceMenuNodes(final HttpServletRequest request, final HttpServletResponse response){
		Long roleId = StringUtil.nullToLong(request.getParameter("roleId"));
		Role role = this.roleManager.get(roleId);
		if(role == null || role.getId() == null){
			return null;
		}

		TreeNode treeNode = new TreeNode ();
		List<Menu> menuList = this.menuManager.getAll();
		if(menuList != null && menuList.size() > 0){
			// 已挂载的访问权限地址
			StringBuffer sql = new StringBuffer ("(select a.*,rr.role_id from ");
			sql.append("(select r.resource_id,r.name,m.menu_id,r.is_enable from jkd_admin_resource r, jkd_admin_menu m where r.menu_id is not null and r.menu_id = m.menu_id) a ");
			sql.append("left join (select role_id,res_id from jkd_admin_role_resource where role_id = ?) rr on rr.res_id = a.resource_id) UNION ");
			sql.append("(select ar.resource_id,ar.name,ar.menu_id,ar.is_enable,rr.role_id from jkd_admin_resource ar left join (select role_id,res_id ");
			sql.append("from jkd_admin_role_resource where role_id = ?) rr on ar.resource_id = rr.res_id where menu_id = -1) ");
			List<Object[]> resourceList = this.resourceManager.querySql(sql.toString(), new Object[]{role.getId(), role.getId()});
			if(resourceList != null && resourceList.size() > 0){
				for(Object[] object : resourceList){
					//父类菜单
					Menu parentMenu = new Menu ();
					parentMenu.setMenuId(StringUtil.nullToLong(object[2]));

					// 模拟当前菜单节点
					Menu menu = new Menu ();
					menu.setMenuId(StringUtil.nullToLong(object[0]));
					menu.setName(StringUtil.null2Str(object[1]));
					menu.setParentMenu(parentMenu);
					menu.setSequence(StringUtil.nullToInteger(menu.getMenuId()));
					menu.setEnableType(StringUtil.nullToBoolean(object[3]) ? 1 : 2);
					menu.setStatus(StringUtil.compareObject(StringUtil.nullToLong(object[4]), role.getId()));
					menu.setIsResource(true);
					menuList.add(menu);
				}
				
				// 访问资源挂载菜单节点
				Menu parentMenu = new Menu ();
				parentMenu.setMenuId(Constants.TOP_DEFUALT_ID);
				
				Menu menu = new Menu ();
				menu.setMenuId(SecurityConstants.MENU_DEFULATE_ID);
				menu.setName("公共访问节点");
				menu.setParentMenu(parentMenu);
				menu.setSequence(Integer.MAX_VALUE);
				menuList.add(menu);
			}


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

		Model model = new ExtendedModelMap();
		model.addAttribute(Constants.MENU_TREE_MAPS, treeNode.getChildrenNode());
		response.setContentType("application/json");
		return new ModelAndView("nodes/resourceMenuNodes", model.asMap());
	}

	/**
	 * 访问权限列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Resource> list = new ArrayList<Resource> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));//排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));//过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));

			// filter过滤字段查询
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Resource.class);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.resourceManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				list = this.resourceManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put("data", list);
		resultMap.put("totalCount", count);
		return resultMap;
	}

	/**
	 * 编辑资源访问权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/editResource")
	public @ResponseBody Map<String, Object> editResource(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String resourceId = request.getParameter("resourceId");
		List<Resource> list = new ArrayList<Resource> ();
		try{
			Resource resource = this.resourceManager.get(StringUtil.nullToLong(resourceId));
			if(resource != null && resource.getResourceId() != null){
				list.add(resource);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		resultMap.put("success", true);
		resultMap.put("total", list.size());
		resultMap.put("data", list.toArray());
		return resultMap;
	}

	/**
	 * 新增访问资源权限
	 * @param resource
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/addResource")
	public @ResponseBody Map<String, Object> addResource(@ModelAttribute("resource")Resource resource, final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		if(StringUtil.isNullStr(resource.getName())){
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", "访问权限名称不能为空");
			return resultMap;
		}else if(StringUtil.isNullStr(resource.getLinkPath())){
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", "访问权限地址不能为空");
			return resultMap;
		}

		try{
			resource.setName(StringUtil.null2Str(resource.getName()));
			resource.setLinkPath(StringUtil.null2Str(resource.getLinkPath()));
			if(this.resourceManager.isExistName(resource.getName(), null)){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "访问权限名称已存在");
				return resultMap;
			}

			// 启用检查访问权限挂载点(必须挂枚举菜单节点)
			if(StringUtil.nullToBoolean(resource.getIsEnable())){
				if(resource.getMenuId() == null 
						|| (!StringUtil.compareObject(resource.getMenuId(), SecurityConstants.MENU_DEFULATE_ID)
								&& !SecurityConstants.MENU_MAP.containsKey(StringUtil.nullToLong(resource.getMenuId())))){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "访问权限未挂载菜单不能直接启用");
					return resultMap;
				}
			}

			resource.setIsEnable(StringUtil.nullToBoolean(resource.getIsEnable()));
			resource.setCreateTime(DateUtil.getCurrentDate());
			resource.setUpdateTime(resource.getCreateTime());
			this.resourceManager.save(resource);

			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.failure"));
			return resultMap;
		}
	}

	/**
	 * 更新访问资源权限
	 * @param resource
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateResource")
	public @ResponseBody Map<String, Object> updateResource(@ModelAttribute("resource")Resource resource, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try{
			Resource tmpResource = null;
			if(resource.getResourceId() == null || (tmpResource = this.resourceManager.get(resource.getResourceId())) == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "编辑对象不存在");
				return resultMap;
			}else if(StringUtil.isNullStr(resource.getName())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "错误,访问权限名称不能为空");
				return resultMap;
			}else if(StringUtil.isNullStr(resource.getLinkPath())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "错误,访问权限地址不能为空");
				return resultMap;
			}

			resource.setName(StringUtil.null2Str(resource.getName()));
			resource.setLinkPath(StringUtil.null2Str(resource.getLinkPath()));
			if(this.resourceManager.isExistName(resource.getName(), resource.getResourceId())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "错误,访问权限名称不能重复");
				return resultMap;
			}

			// 启用检查访问权限挂载点(必须挂枚举菜单节点)
			if(StringUtil.nullToBoolean(resource.getIsEnable())){
				if(resource.getMenuId() == null 
						|| (!StringUtil.compareObject(resource.getMenuId(), SecurityConstants.MENU_DEFULATE_ID)
								&& !SecurityConstants.MENU_MAP.containsKey(StringUtil.nullToLong(resource.getMenuId())))){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "访问权限未挂载菜单不能直接启用");
					return resultMap;
				}
			}

			resource.setIsEnable(StringUtil.nullToBoolean(resource.getIsEnable()));
			resource.setCreateTime(tmpResource.getCreateTime());
			resource.setUpdateTime(DateUtil.getCurrentDate());
			this.resourceManager.save(resource);

			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());

			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.failure"));
			return resultMap;
		}
	}

	/**
	 * 删除访问权限
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteResource")
	public @ResponseBody Map<String, Object> deleteResource(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String record = request.getParameter("resourceGridJson");
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", "请求参数错误或不能为空");
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", "请求服务器异常");
			return resultMap;
		}

		try{
			//批量删除
			List<Resource> list = this.resourceManager.getByIdList(idList);
			if(list != null && list.size() > 0){
				// 检查已经被启用的资源,不能直接删除
				Map<String, Object> paramMap = new HashMap<String, Object> ();
				paramMap.put("isEnable", true);
				paramMap.put("resourceId", idList);
				List<Resource> exitResourceList = this.resourceManager.getHqlPages(paramMap);
				if(exitResourceList != null && exitResourceList.size() > 0){
					StringBuffer existUseErrors = new StringBuffer();
					for(Resource resource : exitResourceList){
						existUseErrors.append("<br>" + resource.getName());
					}

					resultMap.put("success", false);
					resultMap.put("message", String.format("启用状态权限不能直接删除%s", existUseErrors.toString()));
					return resultMap;
				}

				String sql = "delete from jkd_admin_role_resource where res_id in(?)";
				this.resourceManager.executeSql(sql, new Object[]{StringUtil.longArrayToString(idList)});
				this.resourceManager.deleteByIdList(idList);
			}

			resultMap.put("success", true);
			resultMap.put("message", getText("delete.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("delete.failure"));
			return resultMap;
		}
	}

	/**
	 * 更新访问权限状态
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateResourceStatus")
	public @ResponseBody Map<String, Object> updateResourceStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String record = request.getParameter("idListGridJson");
		boolean isEnable = StringUtil.nullToBoolean(request.getParameter("isEnable"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", "请求参数错误或不能为空");
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", "请求服务器异常");
			return resultMap;
		}

		try{
			// 启用检查访问权限挂载点(必须挂枚举菜单节点)
			if(StringUtil.nullToBoolean(isEnable)){
				List<Resource> list = this.resourceManager.getByIdList(idList);
				if(list != null && list.size() > 0){
					boolean isExistError = false;
					StringBuffer existUseErrors = new StringBuffer();
					for(Resource resource : list){
						if(resource.getMenuId() == null 
								|| (!StringUtil.compareObject(resource.getMenuId(), SecurityConstants.MENU_DEFULATE_ID)
										&& !SecurityConstants.MENU_MAP.containsKey(StringUtil.nullToLong(resource.getMenuId())))){
							isExistError = true;
							existUseErrors.append("<br>" + resource.getName());
						}
					}

					if(isExistError){
						resultMap.put("success", false);
						resultMap.put("message", String.format("访问权限未挂载枚举单不能直接启用%s", existUseErrors.toString()));
						return resultMap;
					}
				}
			}

			this.resourceManager.updateEnable(idList, isEnable);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("save.failure"));
			return resultMap;
		}
	}
}
