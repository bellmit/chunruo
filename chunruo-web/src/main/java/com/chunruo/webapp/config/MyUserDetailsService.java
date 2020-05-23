package com.chunruo.webapp.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.User;
import com.chunruo.security.service.UserManager;

@Service
public class MyUserDetailsService implements UserDetailsService {
	private UserManager userManager;

	@Autowired
	MyUserDetailsService(UserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userManager.getUserByName(username);
		if (user == null || user.getUserId() == null) {
			throw new UsernameNotFoundException("未找到此用户");
		}
		
		// 获取用户角色关系
		StringBuffer sql = new StringBuffer("select LOWER(res.link_path),res.menu_id from jkd_admin_group ag, jkd_admin_group_role gr, jkd_admin_role_resource rr, jkd_admin_resource res ");
		sql.append("where %s ag.is_enable = 1 and ag.group_id = gr.group_id and gr.role_id = rr.role_id and rr.res_id = res.resource_id and res.is_enable = 1 ");
		
		String groupId = "";
		if(!StringUtil.nullToBoolean(user.getIsAdmin())) {
			groupId = " ag.group_id = "+user.getGroupId()+" and ";
		}
		List<Object[]> objList = this.userManager.querySql(String.format(sql.toString(), groupId));
		if(objList != null && objList.size() > 0){
			for(int i = 0; i < objList.size(); i ++){
				Object[] object = objList.get(i);
				if(object != null && object.length == 2){
					user.getLinkPathList().add(StringUtil.null2Str(object[0]));
					user.getMenuIdSet().add(StringUtil.nullToLong(object[1]));
				}
			}
		}
		return user;
	}
}
