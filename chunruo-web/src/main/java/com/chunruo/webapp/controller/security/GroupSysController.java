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
import com.chunruo.security.model.Group;
import com.chunruo.security.model.Role;
import com.chunruo.security.model.User;
import com.chunruo.security.service.GroupManager;
import com.chunruo.security.service.RoleManager;
import com.chunruo.security.service.UserManager;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/groupSys/")
public class GroupSysController extends BaseController{
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private GroupManager groupManager;
	@Autowired
	private UserManager userManager;
	
	@RequestMapping(value="/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Group> list = new ArrayList<Group> ();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));//排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));//过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			
			// filter过滤字段查询
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Group.class);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			
			count = this.groupManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				list = this.groupManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(list != null && list.size() > 0){
					for(Group group : list){
						StringBuffer strBuffer = new StringBuffer ();
						if(group.getRoles() != null && group.getRoles().size() > 0){
							boolean isExist = false;
							for(Role role : group.getRoles()){
								if(isExist){
									strBuffer.append(",");
								}
								strBuffer.append(StringUtil.null2Str(role.getName()));
								isExist = true;
							}
						}
						group.setRoles(null);
						group.setRolePath(strBuffer.toString());
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
	 * 编辑群组中角色
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/editGroup")
	public @ResponseBody Map<String, Object> editGroup(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String groupId = request.getParameter("groupId");
		List<Group> list = new ArrayList<Group> ();
		try{
			Group group = this.groupManager.get(StringUtil.nullToLong(groupId));
			if(group != null && group.getGroupId() != null){
				StringBuffer roleIdBuffer = new StringBuffer ();
				if(group.getRoles() != null && group.getRoles().size() > 0){
					boolean isInit = false;
					for(Role role : group.getRoles()){
						if(isInit)roleIdBuffer.append(",");
						roleIdBuffer.append(role.getId());
						isInit = true;
					}
				}
				group.setRoleIds(roleIdBuffer.toString());
				group.setRoles(null);
				list.add(group);
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
	 * 新增群组
	 * @param group
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/addGroup")
	public @ResponseBody Map<String, Object> addGroup(@ModelAttribute("group")Group group, final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		if(StringUtil.isNullStr(group.getName())){
			resultMap.put("error", true);
			resultMap.put("success", true);
			resultMap.put("message", "群组名称不能为空");
			return resultMap;
		}
		
		try{
			group.setName(StringUtil.null2Str(group.getName()));
			if(this.groupManager.isExistName(group.getName(), null)){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "群组名称已存在");
				return resultMap;
			}
			
			Set<Role> roleSet = new HashSet<Role> ();
			if(group.getRoleIds() != null){
				List<Long> idList = StringUtil.stringToLongArray(group.getRoleIds());
				if(idList != null && idList.size() > 0){
					List<Role> roleList = this.roleManager.getByIdList(idList);
					if(roleList != null && roleList.size() > 0){
						for(Role role : roleList){
							roleSet.add(role);
						}
					}
				}
			}
			
			group.setRoles(roleSet);
			group.setIsEnable(StringUtil.nullToBoolean(group.getIsEnable()));
			group.setCreateTime(DateUtil.getCurrentDate());
			group.setUpdateTime(group.getCreateTime());
			this.groupManager.save(group);
			
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
	 * 更新群组信息
	 * @param group
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateGroup")
	public @ResponseBody Map<String, Object> updateGroup(@ModelAttribute("group")Group group, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try{
			Group tmpGroup = null;
			if(group.getGroupId() == null || (tmpGroup = this.groupManager.get(group.getGroupId())) == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "编辑对象不存在");
				return resultMap;
			}else if(StringUtil.isNullStr(group.getName())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "群组名称不能为空");
				return resultMap;
			}
			
			group.setName(StringUtil.null2Str(group.getName()));
			if(this.groupManager.isExistName(group.getName(), tmpGroup.getGroupId())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "群组名称已存在");
				return resultMap;
			}
			
			Set<Role> roleSet = new HashSet<Role> ();
			if(group.getRoleIds() != null){
				List<Long> idList = StringUtil.stringToLongArray(group.getRoleIds());
				if(idList != null && idList.size() > 0){
					List<Role> roleList = this.roleManager.getByIdList(idList);
					if(roleList != null && roleList.size() > 0){
						for(Role role : roleList){
							roleSet.add(role);
						}
					}
				}
			}
			
			group.setRoles(roleSet);
			group.setIsEnable(StringUtil.nullToBoolean(group.getIsEnable()));
			group.setCreateTime(tmpGroup.getCreateTime());
			group.setUpdateTime(DateUtil.getCurrentDate());
			this.groupManager.save(group);
			
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
	 * 删除群组
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteGroup")
	public @ResponseBody Map<String, Object> deleteGroup(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String record = request.getParameter("groupGridJson");
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
			List<Group> list = this.groupManager.getByIdList(idList);
			if(list != null && list.size() > 0){
				boolean isExistError = false;
				StringBuffer errorBuffer = new StringBuffer();
				Map<Long, String> groupMap = new HashMap<Long, String> ();
				for(Group group : list){
					// 启用状态不能直接删除
					if(StringUtil.nullToBoolean(group.getIsEnable())){
						isExistError = true;
						errorBuffer.append("<br>" + group.getName());
					}
					groupMap.put(group.getGroupId(), group.getName());
				}
				
				if(isExistError){
					resultMap.put("success", false);
					resultMap.put("message", String.format("启用群组不能直接删除%s", errorBuffer.toString()));
					return resultMap;
				}
				
				//检查角色是否被用户关联
				List<User> userList = this.userManager.getUserListByGroupIdList(StringUtil.longSetToList(groupMap.keySet()));
				if(userList != null && userList.size() > 0){
					StringBuffer existUseErrors = new StringBuffer();
					int index = 1;
					for(User user : userList){
						existUseErrors.append(String.format("<br>%s: 已绑定用户[%s]", index++, user.getUsername()));
					}
					
					resultMap.put("success", false);
					resultMap.put("message", String.format("群组已被使用%s", existUseErrors.toString()));
					return resultMap;
				}
				this.groupManager.deleteByIdList(idList);
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
	 * 更新群组状态
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateGroupStatus")
	public @ResponseBody Map<String, Object> updateGroupStatus(final HttpServletRequest request) {
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
			this.groupManager.updateEnable(idList, isEnable);
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
