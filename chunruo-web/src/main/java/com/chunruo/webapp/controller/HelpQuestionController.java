package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.HelpQuestion;
import com.chunruo.core.service.HelpQuestionManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/helpQuestion/")
public class HelpQuestionController extends BaseController {
	private Logger log = LoggerFactory.getLogger(HelpQuestionController.class);
	@Autowired
	private HelpQuestionManager helpQuestionManager;

	/**
	 * 帮助与反馈列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<HelpQuestion> helpQuestionList = new ArrayList<HelpQuestion>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), HelpQuestion.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}

			}

			count = this.helpQuestionManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				helpQuestionList = this.helpQuestionManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"),
						sortMap.get("dir"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", helpQuestionList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	/**
	 * 添加
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveHelpQuestion")
	public @ResponseBody Map<String, Object> saveHelpQuestion(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long questionId = StringUtil.nullToLong(request.getParameter("questionId"));
		String name = StringUtil.null2Str(request.getParameter("name")); // 问题名称
		String questionDesc = StringUtil.null2Str(request.getParameter("questionDesc")); // 问题描述
		Integer sort = StringUtil.nullToInteger(request.getParameter("sort"));// 排序
		Integer type = StringUtil.nullToInteger(request.getParameter("type"));// 类型
		Boolean isNoteRed = StringUtil.nullToBoolean(request.getParameter("isNoteRed")); // 是否标红

		try {
			if (StringUtil.isNull(name)) {
				resultMap.put("success", true);
				resultMap.put("error", true);
				resultMap.put("message", "问题名称不能为空");
				return resultMap;
			} else if (StringUtil.isNull(questionDesc)) {
				resultMap.put("success", true);
				resultMap.put("error", true);
				resultMap.put("message", "问题描述不能为空");
				return resultMap;
			}

			HelpQuestion helpQuestion = this.helpQuestionManager.get(questionId);
			if (helpQuestion == null || helpQuestion.getQuestionId() == null) {
				helpQuestion = new HelpQuestion();
				helpQuestion.setCreateTime(DateUtil.getCurrentDate());
			}
			helpQuestion.setName(name);
			helpQuestion.setQuestionDesc(questionDesc);
			helpQuestion.setSort(sort);
			helpQuestion.setType(type);
			helpQuestion.setIsNoteRed(isNoteRed);
			helpQuestion.setUpdateTime(DateUtil.getCurrentDate());
			this.helpQuestionManager.save(helpQuestion);

			resultMap.put("success", true);
			resultMap.put("error", false);
			resultMap.put("message", "保存成功");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}

		resultMap.put("success", true);
		resultMap.put("error", true);
		resultMap.put("message", "保存失败");
		return resultMap;
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteHelpQuestion")
	public @ResponseBody Map<String, Object> deleteHelpQuestion(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));

		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == idList || idList.size() == 0) {
			resultMap.put("success", false);
			resultMap.put("message", "请勾选问题！");
			return resultMap;
		}

		try {
			this.helpQuestionManager.deleteByIdList(idList);
			resultMap.put("success", true);
			resultMap.put("message", this.getText("删除成功"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", this.getText("删除失败"));
		return resultMap;
	}
	
	/**
	 * 退款订单详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getHelpQuestionById")
	public @ResponseBody Map<String, Object> getHelpQuestionById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long questionId = StringUtil.nullToLong(request.getParameter("questionId"));
		HelpQuestion helpQuestion = this.helpQuestionManager.get(questionId);
		if(helpQuestion == null || helpQuestion.getQuestionId() == null){
			resultMap.put("success", true);
			resultMap.put("error", true);
			resultMap.put("message", "记录不存在");
			return resultMap;
		}
		resultMap.put("success", true);
		resultMap.put("error", false);
		resultMap.put("data", helpQuestion);
		return resultMap;
	}

}
