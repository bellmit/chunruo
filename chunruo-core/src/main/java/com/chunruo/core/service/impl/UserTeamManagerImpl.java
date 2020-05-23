package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserTeam;
import com.chunruo.core.repository.UserTeamRepository;
import com.chunruo.core.service.UserTeamManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("userTeamManager")
public class UserTeamManagerImpl extends GenericManagerImpl<UserTeam, Long> implements UserTeamManager {
	private UserTeamRepository userTeamRepository;
	
	@Autowired
	public UserTeamManagerImpl(UserTeamRepository userTeamRepository) {
		super(userTeamRepository);
		this.userTeamRepository = userTeamRepository;
	}

	@Override
	public void updateUserTeamByLoadFunction() {
		this.userTeamRepository.executeSqlFunction("{?=call loadUserTeamList_Fnc()}");
		log.debug("updateUserTeamByLoadFunction======= ");
	}

	@Override
	public List<UserTeam> getUserTeamListByUpdateTime(Date updateTime) {
		return this.userTeamRepository.getUserTeamListByUpdateTime(updateTime);
	}

	@Override
	public List<UserTeam> getUserTeamListByTopUserId(Long topUserId) {
		return this.userTeamRepository.getUserTeamListByTopUserId(topUserId);
	}

	@Override
	public UserTeam getUserTeamByUserId(Long userId) {
		return this.userTeamRepository.getUserTeamByUserId(userId);
	}
	
	/**
	 * 维护用户团队信息
	 * @param isFirstRegister（是否首次注册）
	 */
	@Override
	public void changeUserTeamRecord(UserInfo userInfo) {
		try {
			if (userInfo != null && userInfo.getUserId() != null) {
				// 维护用户团队记录
				UserTeam userTeam = this.userTeamRepository.getUserTeamByUserId(StringUtil.nullToLong(userInfo.getUserId()));
				if (userTeam == null || userTeam.getTeamId() == null) {
					userTeam = new UserTeam();
					userTeam.setUserId(StringUtil.nullToLong(userInfo.getUserId()));
					userTeam.setLogo(StringUtil.null2Str(userInfo.getHeaderImage()));
					userTeam.setCreateTime(DateUtil.getCurrentDate());
				}
				//新建用户补充信息
				userTeam.setExpireEndDate(userInfo.getExpireEndDate());
				userTeam.setUserCreateTime(userInfo.getRegisterTime());
				userTeam.setTopUserId(StringUtil.nullToLong(userInfo.getTopUserId()));
				userTeam.setStoreName(StringUtil.null2Str(userInfo.getStoreName()));
				userTeam.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));
				userTeam.setUpdateTime(DateUtil.getCurrentDate());
				this.userTeamRepository.save(userTeam);
				//更新上级用户下线数量
				UserTeamManagerImpl.updateTopUserTeam(StringUtil.nullToLong(userInfo.getTopUserId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<UserTeam> getUserTeamListByUserIdList(List<Long> userIdList) {
		return this.userTeamRepository.getUserTeamListByUserIdList(userIdList);
	}
	
	/**
	 * 更新上级用户下线数量
	 * @param topUserId
	 */
	public static void updateTopUserTeam(Long topUserId) {
		try {
			UserTeamRepository userTeamRepository = Constants.ctx.getBean(UserTeamRepository.class);
			//更新上级用户下线数量
			UserTeam topUserTeam = userTeamRepository.getUserTeamByUserId(StringUtil.nullToLong(topUserId));
			if (topUserTeam != null && topUserTeam.getTeamId() != null) {
			    StringBuffer strBulSql = new StringBuffer();
			    strBulSql.append("UPDATE jkd_user_team jut,");
			    strBulSql.append("(SELECT ");
			    strBulSql.append("COUNT(CASE WHEN jui.`level` = 1 THEN jui.`user_id` ELSE NULL END) vip_count,");
			    strBulSql.append("COUNT(CASE WHEN jui.`level` = 2 THEN jui.`user_id` ELSE NULL END) declare_count,");
			    strBulSql.append("COUNT(CASE WHEN jui.`level` = 3 THEN jui.`user_id` ELSE NULL END) agent_count,");
			    strBulSql.append("COUNT(CASE WHEN jui.`level` = 4 THEN jui.`user_id` ELSE NULL END) v2_declare_count,");
			    strBulSql.append("COUNT(CASE WHEN jui.`level` = 5 THEN jui.`user_id` ELSE NULL END) v3_declare_count,");
			    strBulSql.append("jui.top_user_id ");
			    strBulSql.append("FROM jkd_user_info jui WHERE jui.`is_agent` = 1 AND jui.status = 1 AND jui.`top_user_id` = %s ) aa ");
			    strBulSql.append("SET jut.vip_count = aa.vip_count,jut.`declare_count` = aa.declare_count,");
			    strBulSql.append("jut.`agent_count` = aa.agent_count,jut.`v2_declare_count` = aa.v2_declare_count,jut.`v3_declare_count` = aa.v3_declare_count,");
			    strBulSql.append("jut.`update_time` = NOW() WHERE jut.user_id = aa.top_user_id");
			    userTeamRepository.executeSql(String.format(strBulSql.toString(),StringUtil.nullToLong(topUserTeam.getUserId())));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
