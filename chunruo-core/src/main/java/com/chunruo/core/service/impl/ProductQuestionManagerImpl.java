package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductQuestion;
import com.chunruo.core.repository.ProductQuestionRepository;
import com.chunruo.core.service.ProductAnswerManager;
import com.chunruo.core.service.ProductQuestionManager;

@Transactional
@Component("productQuestionManager")
public class ProductQuestionManagerImpl extends GenericManagerImpl<ProductQuestion, Long> implements ProductQuestionManager{
	private ProductQuestionRepository productQuestionRepository;
	@Autowired
	private ProductAnswerManager productAnswerManager;
	@Autowired
	public ProductQuestionManagerImpl(ProductQuestionRepository productQuestionRepository) {
		super(productQuestionRepository);
		this.productQuestionRepository = productQuestionRepository;
	}

	@Override
	public List<ProductQuestion> getQuestionListByProductId(Long productId) {
		return this.productQuestionRepository.getQuestionListByProductId(productId);
	}

	@Override
	public List<ProductQuestion> getQuestionListByUpdateTime(Date updateTime) {
		return this.productQuestionRepository.getQuestionListByUpdateTime(updateTime);
	}
	
	@Override
	public void updateQuestionStatusByIdList(List<Long> idList, boolean status){
		if(idList != null && idList.size() > 0){
			this.productQuestionRepository.updateQuestionStatusByIdList(idList, status);
		}
	}

	/**
	 * 批量删除问题，并且删除与之对应的答案
	 */
	@Override
	public void deleteQuestionByIdList(List<Long> idList) {
		if(idList != null && idList.size() > 0){
			this.productQuestionRepository.deleteQuestionByIdList(idList, true);
			this.productAnswerManager.deleteAnswerByQuestionIdList(idList);
		}
	}
}
