package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.ProductIntro;
import com.chunruo.core.repository.ProductIntroRepository;
import com.chunruo.core.service.ProductIntroManager;

@Transactional
@Component("productIntroManager")
public class ProductIntroManagerImpl extends GenericManagerImpl<ProductIntro, Long> implements ProductIntroManager{
	private ProductIntroRepository productIntroRepository;

	@Autowired
	public ProductIntroManagerImpl(ProductIntroRepository productIntroRepository) {
		super(productIntroRepository);
		this.productIntroRepository = productIntroRepository;
	}

	@Override
	public List<ProductIntro> getProductIntroListByUpdateTime(Date updateTime) {
		return this.productIntroRepository.getProductIntroListByUpdateTime(updateTime);
	}

}
