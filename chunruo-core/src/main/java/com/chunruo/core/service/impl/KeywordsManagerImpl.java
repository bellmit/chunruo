package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Keywords;
import com.chunruo.core.repository.KeywordsRepository;
import com.chunruo.core.service.KeywordsManager;

@Transactional
@Component("keywordsManager")
public class KeywordsManagerImpl extends GenericManagerImpl<Keywords, Long>  implements KeywordsManager{
	private KeywordsRepository keywordsRepository;
	
	@Autowired
	public KeywordsManagerImpl(KeywordsRepository keywordsRepository) {
		super(keywordsRepository);
		this.keywordsRepository = keywordsRepository;
	}

	@Override
	public Keywords getKeywordsByIsDefault(Boolean isDefault) {
		List<Keywords> list = this.keywordsRepository.getKeywordsByIsDefault(isDefault);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public void updateKeywordsDefault(boolean isDefault, Long keywordsId) {
		this.keywordsRepository.updateKeywordsDefault(isDefault, keywordsId);
	}
	
}
