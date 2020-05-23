package com.chunruo.webapp.controller.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.MatcherUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Group;
import com.chunruo.security.model.User;
import com.chunruo.security.service.GroupManager;
import com.chunruo.security.service.UserManager;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/userSys")
public class UserSysController extends BaseController{
	@Autowired
	private UserManager userManager;
	@Autowired
	private GroupManager groupManager;
	
	@RequestMapping("/list")
	public @ResponseBody Map<String,Object> list(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<User> adminUserList = new ArrayList<User>();
		Long count = 0L;
		try {

			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));//排序
			String filters = StringUtil.nullToString(request.getParameter("filters"));//过滤
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			
			// filter过滤字段查询
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), User.class);
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
				
				// 检查是否查询群组名称
				if(paramMap.containsKey("groupName")){
					String groupName = StringUtil.null2Str(paramMap.remove("groupName"));
					Map<String, Object> params = new HashMap<String, Object> ();
					params.put("name", groupName);
					List<Group> groupList = this.groupManager.getHqlPages(params);
					if(groupList != null && groupList.size() > 0){
						Set<Long> groupIdSet = new HashSet<Long> ();
						for(Group group : groupList){
							groupIdSet.add(group.getGroupId());
						}
						paramMap.put("groupId", StringUtil.longSetToList(groupIdSet));
					}
				}
			}

			count = this.userManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				adminUserList = this.userManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(adminUserList != null && adminUserList.size() > 0){
					Set<Long> groupIdSet = new HashSet<Long> ();
					for(User user : adminUserList){
						groupIdSet.add(StringUtil.nullToLong(user.getGroupId()));
					}
					
					// 用户群组
					List<Group> groupList = this.groupManager.getByIdList(StringUtil.longSetToList(groupIdSet));
					if(groupList != null && groupList.size() > 0){
						Map<Long, String> groupMap = new HashMap<Long, String> ();
						for(Group group : groupList){
							groupMap.put(group.getGroupId(), group.getName());
						}
						
						// 回填到用户
						for(User user : adminUserList){
							if(groupMap.containsKey(StringUtil.nullToLong(user.getGroupId()))){
								user.setGroupName(groupMap.get(StringUtil.nullToLong(user.getGroupId())));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", adminUserList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	} 
	
	/**
	 * 编辑自己用户
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/myUser")
	public @ResponseBody Map<String, Object> myUser(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<User> list = new ArrayList<User> ();
		try{
			User user = this.getCurrentUser(request);
			if(user != null && user.getUserId() != null){
				User dbUser = this.userManager.get(user.getUserId());
				if(dbUser != null && dbUser.getUserId() != null){
					dbUser.setConfirmPassword(dbUser.getPassword());
					list.add(dbUser);
				}
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
	 * 编辑用户
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/editUser")
	public @ResponseBody Map<String, Object> editUser(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String userId = request.getParameter("userId");
		List<User> list = new ArrayList<User> ();
		try{
			User user = this.userManager.get(StringUtil.nullToLong(userId));
			if(user != null && user.getUserId() != null){
				user.setConfirmPassword(user.getPassword());
				list.add(user);
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
	 * 编辑自己的用户信息
	 * @return
	 */
	@RequestMapping(value = "/updateMyUser")
	public @ResponseBody Map<String,Object> updateMyUser(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String password = StringUtil.null2Str(request.getParameter("password"));
		String confirmPassword = StringUtil.null2Str(request.getParameter("confirmPassword"));
		Boolean sex = StringUtil.nullToBoolean(request.getParameter("sex"));
		String realname = StringUtil.null2Str(request.getParameter("realname"));
		String email = StringUtil.null2Str(request.getParameter("email"));

		try {
			if (StringUtil.isNull(realname)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "请填写真实姓名");
				return resultMap;
			} else if (!StringUtil.compareObject(password, confirmPassword)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "确认密码不一致");
				return resultMap;
			}

			// 此用户名不存在
			User dbUser = this.userManager.get(this.getCurrentUser(request).getUserId());
			if(dbUser == null || dbUser.getUserId() == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "此用户名不存在");
				return resultMap;
			}
			
			// 原始密码不一致才需要保存
			if(!StringUtil.compareObject(dbUser.getPassword(), password)){
				dbUser.setPassword(new BCryptPasswordEncoder().encode(password));
			}
			dbUser.setSex(StringUtil.nullToBoolean(sex));
			dbUser.setRealname(realname);
			dbUser.setEmail(email);
			dbUser.setUpdateTime(DateUtil.getCurrentDate());
			this.userManager.save(dbUser);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "保存失败");
		return resultMap;
	}
	
	/**
	 * 添加管理员
	 * @return
	 */
	@RequestMapping(value = "/saveAdminUser")
	public @ResponseBody Map<String,Object> saveAdminUser(final HttpServletRequest request){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long userId = StringUtil.nullToLong(request.getParameter("userId"));
		String username = StringUtil.null2Str(request.getParameter("username"));
		String password = StringUtil.null2Str(request.getParameter("password"));
		String confirmPassword = StringUtil.null2Str(request.getParameter("confirmPassword"));
		Boolean sex = StringUtil.nullToBoolean(request.getParameter("sex"));
		Integer level = StringUtil.nullToInteger(request.getParameter("level"));
		String realname = StringUtil.null2Str(request.getParameter("realname"));
		String mobile = StringUtil.null2Str(request.getParameter("mobile"));
		String email = StringUtil.null2Str(request.getParameter("email"));
		Long groupId = StringUtil.nullToLong(request.getParameter("groupId"));

		try {
			if (StringUtil.isNull(username) || StringUtil.isNull(password)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "用户名或密码不能为空");
				return resultMap;
			} else if (!MatcherUtil.matcherAccount(username)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "用户名只支持5-16位非汉字格式");
				return resultMap;
			} else if (StringUtil.isNull(realname)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "请填写真实姓名");
				return resultMap;
			} else if (!StringUtil.isValidateMobile(mobile)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "手机号码无效");
				return resultMap;
			} else if (!StringUtil.compareObject(password, confirmPassword)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "确认密码不一致");
				return resultMap;
			}else if(StringUtil.compareObject(0, groupId)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "请选择所属群组");
				return resultMap;
			}

			List<Integer> levelList = new ArrayList<Integer>();
			levelList.add(User.ADMIN_USER_LEVEL_CUSTOMER);
			levelList.add(User.ADMIN_USER_LEVEL_FINANCE);
			levelList.add(User.ADMIN_USER_LEVEL_MANAGER);
			if (!levelList.contains(level)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "权限级别错误");
				return resultMap;
			}
			
			User adminUser = this.userManager.get(userId);
			if (adminUser == null || adminUser.getUserId() == null) {
				User user = this.userManager.getUserByName(username);
				if (user != null && user.getUserId() != null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "此用户名已存在");
					return resultMap;
				}
				
				adminUser = new User();
				adminUser.setPassword(new BCryptPasswordEncoder().encode(password));
				adminUser.setUsername(username);
				adminUser.setCreateTime(DateUtil.getCurrentDate());
				adminUser.setIsAdmin(true);
				adminUser.setEnabled(true);
				adminUser.setAccountExpired(false);
				adminUser.setCredentialsExpired(false);
				adminUser.setAccountLocked(false);
				adminUser.setVersion(4);
			}else{
				// 原始密码不一致才需要保存
				if(!StringUtil.compareObject(adminUser.getPassword(), password)){
					adminUser.setPassword(new BCryptPasswordEncoder().encode(password));
				}
			}
			
			adminUser.setGroupId(groupId);
			adminUser.setSex(StringUtil.nullToBoolean(sex));
			adminUser.setLevel(level);
			adminUser.setRealname(realname);
			adminUser.setMobile(mobile);
			adminUser.setEmail(email);
			adminUser.setUpdateTime(DateUtil.getCurrentDate());
			this.userManager.save(adminUser);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "保存失败");
		return resultMap;
	}
	
	
	/**
	 * 删除管理员
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteAdminUser")
	public @ResponseBody Map<String, Object> setCustomerManager(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("userIdGridJson"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			this.userManager.deleteByIdList(idList);
			
			resultMap.put("success", true);
           	resultMap.put("message", getText("删除成功"));
           	return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "错误，删除失败");
		return resultMap;
	}
}
