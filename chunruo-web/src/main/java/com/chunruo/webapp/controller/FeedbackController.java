package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.Feedback;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.FeedbackManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/feedback/")
public class FeedbackController extends BaseController {
	@Autowired
	private FeedbackManager feedbackManager;
	@Autowired
	private UserInfoManager userInfoManager;

	@RequestMapping(value = "/list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Feedback> list = new ArrayList<Feedback>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Feedback.class);

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.feedbackManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				list = this.feedbackManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if (list != null && list.size() > 0) {
					//检查当前entity是否为托管状态（Managed），若为托管态，需要转变为游离态，这样entity的数据发生改变时，就不会自动同步到数据库中
					//游离态的entity需要调用merge方法转为托管态。
					this.feedbackManager.detach(list);
					
					List<Long> userIdList = new ArrayList<Long>();
					for (Feedback feedback : list) {
						feedback.setMobile(StringUtil.mobileFormat(feedback.getMobile()));
						Long userId = feedback.getUserId();
						if (!userIdList.contains(userId)) {
							userIdList.add(userId);
						}
					}

					// 所有用户
					Map<Long, UserInfo> userMap = new HashMap<Long, UserInfo>();
					List<UserInfo> userInfoList = this.userInfoManager.getByIdList(userIdList);
					if (userInfoList != null && userInfoList.size() > 0) {
						for (UserInfo user : userInfoList) {
							userMap.put(user.getUserId(), user);
						}
					}

					for (Feedback feedback : list) {
						Long userId = feedback.getUserId();
						UserInfo userInfo = userMap.get(userId);
						if (userInfo != null && userInfo.getUserId() != null) {
							feedback.setUserName(userInfo.getNickname());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", list);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	@RequestMapping(value = "/getFeedbackById")
	public @ResponseBody Map<String, Object> getFeedbackById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long feedbackId = StringUtil.nullToLong(request.getParameter("feedbackId"));
		Feedback feedback = new Feedback();

		try {
			feedback = this.feedbackManager.get(feedbackId);
			UserInfo userInfo = userInfoManager.get(feedback.getUserId());
            if(userInfo != null && userInfo.getUserId() != null) {
            	//检查当前entity是否为托管状态（Managed），若为托管态，需要转变为游离态，这样entity的数据发生改变时，就不会自动同步到数据库中
				//游离态的entity需要调用merge方法转为托管态。
				this.feedbackManager.detach(feedback);
				
				feedback.setMobile(StringUtil.mobileFormat(feedback.getMobile()));
            	feedback.setUserName(userInfo.getNickname());
            }
			
			
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("feedback", feedback);
		return resultMap;
	}

	@RequestMapping(value = "/replyMsg")
	public @ResponseBody Map<String, Object> replyMsg(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long feedbackId = StringUtil.nullToLong(request.getParameter("feedbackId"));
		String replyMsg = StringUtil.nullToString(request.getParameter("replyMsg"));

		try {
			Feedback feedback = this.feedbackManager.get(feedbackId);
			if (feedback == null || feedback.getFeedbackId() == null) {
				resultMap.put("success", false);
				resultMap.put("msg", "错误,反馈记录不存在");
				return resultMap;
			} else if (StringUtil.isNullStr(replyMsg)) {
				resultMap.put("success", false);
				resultMap.put("msg", "请填写回复内容");
				return resultMap;
			}

			feedback.setReplyMsg(replyMsg);
			feedback.setIsReply(Feedback.REPLYED);
			feedback.setIsPushUser(true);
			feedback.setUpdateTime(new Date());

			feedbackManager.update(feedback);
			resultMap.put("success", true);
			resultMap.put("msg", "操作成功");
			return resultMap;

		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("msg", "错误,操作失败");
		return resultMap;
	}

}
