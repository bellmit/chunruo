package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoListByTopUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserProfitByUserIdCacheManager;
import com.chunruo.cache.portal.impl.UserTeamListByTopUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserProfitRecord;
import com.chunruo.core.model.UserTeam;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.vo.TagModel;

public class MyTeamTag extends BaseTag {

	public TagModel<Map<String, Object>> getData(Object pageidx_1, Object pagesize_1, Object lastId_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);

		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE * 2;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<Map<String, Object>> tagModel = new TagModel<Map<String, Object>>();
		try {
			// 检查用户是否登录
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}
			
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			UserProfitByUserIdCacheManager userProfitByUserIdCacheManager = Constants.ctx.getBean(UserProfitByUserIdCacheManager.class);
			UserTeamListByTopUserIdCacheManager userTeamListByTopUserIdCacheManager = Constants.ctx.getBean(UserTeamListByTopUserIdCacheManager.class);
			UserInfoListByTopUserIdCacheManager userInfoListByTopUserIdCacheManager = Constants.ctx.getBean(UserInfoListByTopUserIdCacheManager.class);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			List<UserInfo> userInfoList = userInfoListByTopUserIdCacheManager.getSession(StringUtil.nullToLong(userInfo.getUserId()));
			if(userInfoList != null && !userInfoList.isEmpty()) {
				Collections.sort(userInfoList,new Comparator<UserInfo>() {
					@Override
					public int compare(UserInfo o1, UserInfo o2) {
						Long time1 = o1.getUpgradeTime() == null ? 0L : o1.getUpgradeTime().getTime();
						Long time2 = o2.getUpgradeTime() == null ? 0L : o2.getUpgradeTime().getTime();
						return -time1.compareTo(time2);
					}
				});
				
				Map<Long,UserInfo> userInfoMap = new HashMap<Long,UserInfo>();
				List<Long> userIdList = new ArrayList<Long>();
				for(UserInfo user : userInfoList) {
					if(!StringUtil.compareObject(user.getLevel(), UserLevel.USER_LEVEL_DEALER)) {
						continue;
					}
					userIdList.add(StringUtil.nullToLong(user.getUserId()));
					userInfoMap.put(user.getUserId(), user);
				}
				
				/**
				 * 自动List分页工具
				 */
				ListPageUtil<UserInfo> pageUtil = new ListPageUtil<UserInfo>() {
					@Override
					public UserInfo addObject(Long objectId) {
						// 返回对象自定义
						UserInfo downUser = userInfoMap.get(objectId);
						if(downUser != null && downUser.getUserId() != null){
							//用户头像处理
							String logo = StringUtil.null2Str(downUser.getHeaderImage());
//							downUser.setLogo(UserInfoTag.getUserHeaderImage(logo, request));
						}
						return downUser;
					}
				};

//				/**
//				 * 返回自动分页结果
//				 */
//				ListPageVo<List<UserTeam>> listPageVo = pageUtil.getPageList(resultList, lastId, pageidx, pagesize);
//				if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
//					StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
//					urls.append("pageidx=" + (++pageidx) + "&");
//					urls.append("pagesize=" + pagesize + "&");
//					urls.append("lastId=" + listPageVo.getLastId());
//					tagModel.setNextPageURL(urls.toString());
//				}
				
			}
			
			
			
			
			
			tagModel.setData(resultMap);
			tagModel.setCode(PortalConstants.CODE_SUCCESS);
			return tagModel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		tagModel.setMsg("服务器错误");
		tagModel.setCode(PortalConstants.CODE_ERROR);
		return tagModel;
	}

}
