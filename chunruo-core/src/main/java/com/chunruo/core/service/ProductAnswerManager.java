package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductAnswer;

public interface ProductAnswerManager extends GenericManager<ProductAnswer, Long>{

	public List<ProductAnswer> getAnswerListByQuestionId(Long questionId);
	
	public List<ProductAnswer> getAnswerListByUpdateTime(Date updateTime);

	void updateAnswerStatusByIdList(List<Long> idList, boolean status);
	
	void deleteAnswerByIdList(List<Long> idList);
	
	void deleteAnswerByQuestionIdList(List<Long> questionIdList);
}
