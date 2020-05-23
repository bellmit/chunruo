package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductAnswer;
import com.chunruo.core.repository.ProductAnswerRepository;
import com.chunruo.core.service.ProductAnswerManager;

@Transactional
@Component("productAnswerManager")
public class ProductAnswerManagerImpl extends GenericManagerImpl<ProductAnswer, Long> implements ProductAnswerManager {
	private ProductAnswerRepository productAnswerRepository;

	@Autowired
	public ProductAnswerManagerImpl(ProductAnswerRepository productAnswerRepository) {
		super(productAnswerRepository);
		this.productAnswerRepository = productAnswerRepository;
	}

	@Override
	public List<ProductAnswer> getAnswerListByQuestionId(Long questionId) {
		return this.productAnswerRepository.getAnswerListByQuestionId(questionId);
	}

	@Override
	public List<ProductAnswer> getAnswerListByUpdateTime(Date updateTime) {
		return this.productAnswerRepository.getAnswerListByUpdateTime(updateTime);
	}

	@Override
	public void updateAnswerStatusByIdList(List<Long> idList, boolean status) {
		this.productAnswerRepository.updateAnswerStatusByIdList(idList, status);
	}

	@Override
	public void deleteAnswerByIdList(List<Long> idList) {
		this.productAnswerRepository.deleteAnswerByIdList(idList, true);
	}

	@Override
	public void deleteAnswerByQuestionIdList(List<Long> questionIdList) {
		this.productAnswerRepository.deleteAnswerByQuestionIdList(questionIdList, true);
	}
}
