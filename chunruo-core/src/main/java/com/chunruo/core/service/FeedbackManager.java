package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Feedback;

public interface FeedbackManager extends GenericManager<Feedback, Long>{

	public List<Feedback> getFeedbackListByUserId(Long userId);

	public List<Feedback> getFeedbackListByUpdateTime(Date updateTime);

}
