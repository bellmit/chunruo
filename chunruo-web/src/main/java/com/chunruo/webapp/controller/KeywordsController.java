package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.Constants;
import com.chunruo.core.model.Keywords;
import com.chunruo.core.service.KeywordsManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/keywords/")
public class KeywordsController extends BaseController{
	private Logger log = LoggerFactory.getLogger(KeywordsController.class);
	@Autowired
	private KeywordsManager keywordsManager;
	
	/**
	 * 关键词列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/keywordsList")
	public @ResponseBody Map<String, Object> keywordsList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Keywords> keywordsList = new ArrayList<Keywords>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Keywords.class);

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.keywordsManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				keywordsList = this.keywordsManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("data", keywordsList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	/**
	 * 保存关键词
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveKeywords")
	public @ResponseBody Map<String, Object> saveKeywords(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String name = StringUtil.nullToString(request.getParameter("name"));
		try {
			List<Keywords> keywordsList= keywordsManager.getAll();
			if (keywordsList != null && keywordsList.size() > 0) {
				for (Keywords words : keywordsList) {
					if (StringUtil.nullToBoolean(words.getIsDefault())) {
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("已有默认关键词，请先取消!!!"));
						return resultMap;
					}
				}
			}
			//判断优惠券名称不得超过十个汉字
			byte[] bytes = name.getBytes("UTF-8");
			if (bytes.length > 24) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("关键词名称不能超过8个字!!!"));
				return resultMap;
			}else if (name == null || "".equals(name)) {
				//优惠券名称不能为空
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("关键词名称不能为空"));
				return resultMap;
			}
			//判断输入的关键词名称是否已经有
			if (keywordsList != null && keywordsList.size() > 0) {
				for (Keywords words : keywordsList) {
					//若存在，状态改为默认
					if (Objects.equals(words.getName(), name)) {
						words.setIsDefault(true);
						words.setUpdateTime(new Date());
						keywordsManager.update(words);
						resultMap.put("error", false);
						resultMap.put("success", true);
						resultMap.put("message", getText("save.success"));
						return resultMap;
					}
				}
			}
			Keywords keywords = new Keywords();
			keywords.setSeekCount(0);
			keywords.setName(name);
			keywords.setIsDefault(true);
			keywords.setCreateTime(DateUtil.getCurrentDate());
			keywords.setUpdateTime(keywords.getCreateTime());
			keywords = keywordsManager.save(keywords);
			
			//更改关键词缓存
			if (StringUtil.nullToBoolean(keywords.getIsDefault())) {
				Constants.DEFAULT_KEYWORDS = keywords;
			}
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("保存失败"));
		return resultMap;
	}
	
	/**
	 * 设置关键词状态
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setKeywordsStatus")
	public @ResponseBody Map<String, Object> setSendStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long keywordsId = StringUtil.nullToLong(request.getParameter("keywordsId"));
		Boolean status = StringUtil.nullToBoolean(request.getParameter("status"));
		try {
			if (status) {
				//判断输入的关键词名称是否已经有
				List<Keywords> keywordsList= keywordsManager.getAll();
				if (keywordsList != null && keywordsList.size() > 0) {
					for (Keywords words : keywordsList) {
						if (StringUtil.nullToBoolean(words.getIsDefault())) {
							resultMap.put("error", true);
							resultMap.put("success", true);
							resultMap.put("message", getText("已有默认关键词，请先取消!!!"));
							return resultMap;
						}
					}
				}
			}
			Keywords keywords = this.keywordsManager.get(keywordsId);
			keywords.setIsDefault(status);
			keywords.setUpdateTime(new Date());
			keywordsManager.update(keywords);
			
			//更改关键词缓存
			if (StringUtil.nullToBoolean(keywords.getIsDefault())) {
				Constants.DEFAULT_KEYWORDS = keywords;
			}
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
		return resultMap;
	}
	
}
