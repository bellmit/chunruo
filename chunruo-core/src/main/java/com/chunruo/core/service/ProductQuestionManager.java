package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductQuestion;

public interface ProductQuestionManager extends GenericManager<ProductQuestion, Long>{

	public List<ProductQuestion> getQuestionListByProductId(Long productId);
	
	public List<ProductQuestion> getQuestionListByUpdateTime(Date updateTime);

	public void updateQuestionStatusByIdList(List<Long> idList, boolean status);
	
	public void deleteQuestionByIdList(List<Long> idList);
}
