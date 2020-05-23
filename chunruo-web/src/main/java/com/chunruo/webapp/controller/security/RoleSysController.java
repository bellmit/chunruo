package com.chunruo.webapp.controller.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Resource;
import com.chunruo.security.model.Role;
import com.chunruo.security.service.ResourceManager;
import com.chunruo.security.service.RoleManager;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/roleSys/")
public class RoleSysController extends BaseController {
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private ResourceManager resourceManager;

	/**
	 * 获取角色资源列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Role> list = new ArrayList<Role> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));//排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));//过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			
			// filter过滤字段查询
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Role.class);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			count = this.roleManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				list = this.roleManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(list != null && list.size() > 0){
					for(Role r : list){
						r.setResources(null);
						r.setGroups(null);
					}
				}
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
	 * 获取所有的角色列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getRoleList")
	public @ResponseBody Map<String, Object> getRoleList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Role> roleList = new ArrayList<Role> ();
		try{
			roleList = this.roleManager.getAll();
			if(roleList != null && roleList.size() > 0){
				for(Role role : roleList){
					role.setGroups(null);
					role.setResources(null);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("success", true);
		resultMap.put("roleList", roleList);
		return resultMap;
	}

	/**
	 * 编辑角色
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/editRole")
	public @ResponseBody Map<String, Object> editRole(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Role> list = new ArrayList<Role> ();
		try{
			String roleId = request.getParameter("roleId");
			Role role = this.roleManager.get(StringUtil.nullToLong(roleId));
			if(role != null && role.getId() != null){
				role.setResources(null);
				role.setGroups(null);
				list.add(role);
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
	 * 保存角色
	 * @param role
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveRole")
	public @ResponseBody Map<String, Object> updateRole(@ModelAttribute("role")Role role, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try{
			Role tmpRole = null;
			boolean isNewCreate = (role.getId() == null);
			if(!StringUtil.nullToBoolean(isNewCreate)){
				if((tmpRole = this.roleManager.get(role.getId())) == null){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "编辑角色对象不存在");
					return resultMap;
				}
			}
			
			// 检查角色名称是否为空
			if(StringUtil.isNullStr(role.getName())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "角色名称不能为空");
				return resultMap;
			}
			
			// 检查角色名称是否已存在
			role.setName(StringUtil.null2Str(role.getName()));
			if(this.roleManager.isExistName(role.getName(), role.getId())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "角色名称已存在");
				return resultMap;
			}

			role.setCreateTime(DateUtil.getCurrentDate());
			role.setUpdateTime(DateUtil.getCurrentDate());
			if(!StringUtil.nullToBoolean(isNewCreate)){
				// 更新操作保留之前的配置
				role.setResources(tmpRole.getResources());
				role.setGroups(tmpRole.getGroups());
				role.setCreateTime(tmpRole.getCreateTime());
			}
			this.roleManager.save(role);
			
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
	 * 角色与资源关系保存
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveResourceToRole")
	public @ResponseBody Map<String, Object> saveResourceToRole(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long roleId = StringUtil.nullToLong(request.getParameter("roleId"));
		String record = request.getParameter("idListGridJson");
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", "请求参数错误或不能为空");
			return resultMap;
		}

		try{
			Role role = this.roleManager.get(roleId);
			if(role == null || role.getId() == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "角色对象不存在");
				return resultMap;
			}

			Set<Resource> resSet = new HashSet<Resource> ();
			if(idList != null && idList.size() > 0){
				List<Resource> list = this.resourceManager.getByIdList(idList);
				if(list != null && list.size() > 0){
					for(Resource resource : list){
						resSet.add(resource);
					}
				}
			}

			role.setResources(resSet);
			this.roleManager.save(role);
		
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
