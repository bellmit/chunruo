package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.HelpQuestion;

public interface HelpQuestionManager extends GenericManager<HelpQuestion, Long> {

	public List<HelpQuestion> getHelpQuestionListByUpdateTime(Date updateTime);


}
