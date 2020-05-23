package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.HelpQuestion;
import com.chunruo.core.repository.HelpQuestionRepository;
import com.chunruo.core.service.HelpQuestionManager;


@Component("helpQuestionManager")
public class HelpQuestionManagerImpl extends GenericManagerImpl<HelpQuestion, Long> implements HelpQuestionManager{
	private HelpQuestionRepository helpQuestionRepository;

	@Autowired
	public HelpQuestionManagerImpl(HelpQuestionRepository helpQuestionRepository) {
		super(helpQuestionRepository);
		this.helpQuestionRepository = helpQuestionRepository;
	}

	@Override
	public List<HelpQuestion> getHelpQuestionListByUpdateTime(Date updateTime) {
		return this.helpQuestionRepository.getHelpQuestionListByUpdateTime(updateTime);
	}


}
