package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Feedback;
import com.chunruo.core.repository.FeedbackRepository;
import com.chunruo.core.service.FeedbackManager;

@Transactional
@Component("feedbackManager")
public class FeedbackManagerImpl extends GenericManagerImpl<Feedback, Long> implements FeedbackManager{
	private FeedbackRepository feedbackRepository;

	@Autowired
	public FeedbackManagerImpl(FeedbackRepository feedbackRepository) {
		super(feedbackRepository);
		this.feedbackRepository = feedbackRepository;
	}

	@Override
	public List<Feedback> getFeedbackListByUserId(Long userId) {
		// TODO Auto-generated method stub
		return this.feedbackRepository.getFeedbackListByUserId(userId);
	}

	@Override
	public List<Feedback> getFeedbackListByUpdateTime(Date updateTime) {
		return this.feedbackRepository.getFeedbackListByUpdateTime(updateTime);
	}

}
