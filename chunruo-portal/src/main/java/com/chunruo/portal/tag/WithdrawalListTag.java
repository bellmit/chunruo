package com.chunruo.portal.tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chunruo.cache.portal.impl.UserWithdrawalListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserWithdrawal;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.vo.TagModel;

public class WithdrawalListTag extends BaseTag {
	

	public TagModel<List<UserWithdrawal>> getData(Object pageidx_1, Object pagesize_1, Object lastId_1, Object status_1) {
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
		Integer status = StringUtil.nullToInteger(status_1);

		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<UserWithdrawal>> tagModel = new TagModel<List<UserWithdrawal>>();
		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}

			List<Long> recordIdList = new ArrayList<Long>();
			UserWithdrawalListByUserIdCacheManager userWithdrawalListByUserIdCacheManager = Constants.ctx.getBean(UserWithdrawalListByUserIdCacheManager.class);
			final Map<String, UserWithdrawal> recordIdMap = userWithdrawalListByUserIdCacheManager.getSession(userInfo.getUserId());
			if (recordIdMap != null && recordIdMap.size() > 0) {
				// 排序
				List<Map.Entry<String, UserWithdrawal>> mappingList = new ArrayList<Map.Entry<String, UserWithdrawal>>(
						recordIdMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, UserWithdrawal>>() {
					public int compare(Map.Entry<String, UserWithdrawal> obj1, Map.Entry<String, UserWithdrawal> obj2) {
						UserWithdrawal record1 = obj1.getValue();
						UserWithdrawal record2 = obj2.getValue();
						if (record1 == null || record1.getCreateTime() == null) {
							return -1;
						} else if (record2 == null || record2.getCreateTime() == null) {
							return -1;
						}
						return (record1.getCreateTime().getTime() < record2.getCreateTime().getTime()) ? 1 : -1;
					}
				});

				for (Map.Entry<String, UserWithdrawal> entry : mappingList) {
					UserWithdrawal record = entry.getValue();
					if(!StringUtil.compareObject(status, StringUtil.nullToInteger(record.getStatus()))
							&& !StringUtil.compareObject(status, 0)) {
						continue;
					}
					if (StringUtil.compareObject(record.getUserId(), userInfo.getUserId())) {
						recordIdList.add(StringUtil.nullToLong(entry.getKey()));
					}
				}
			}

			/**
			 * 自动List分页工具
			 */
			ListPageUtil<UserWithdrawal> pageUtil = new ListPageUtil<UserWithdrawal>() {
				@Override
				public UserWithdrawal addObject(Long objectId) {
					// 返回对象自定义
					return recordIdMap.get(StringUtil.null2Str(objectId));
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<UserWithdrawal>> listPageVo = pageUtil.getPageList(recordIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}

			Map<String, Object> dataMap = new HashMap<String, Object>();
			BigDecimal balance = new BigDecimal(StringUtil.nullToDoubleFormatStr(userInfo.getBalance()));
			
			dataMap.put("balance", StringUtil.null2Str(balance.toString()));
			tagModel.setDataMap(dataMap);
			tagModel.setData(listPageVo.getDataList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}
