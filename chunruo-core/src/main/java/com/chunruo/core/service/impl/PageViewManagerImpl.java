package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.PageView;
import com.chunruo.core.repository.PageViewRepository;
import com.chunruo.core.service.PageViewManager;

@Transactional
@Component("pageViewManager")
public class PageViewManagerImpl extends GenericManagerImpl<PageView, Long>  implements PageViewManager {
  
	private PageViewRepository pageViewRepository;
	
	@Autowired
	public PageViewManagerImpl(PageViewRepository pageViewRepository) {
		super(pageViewRepository);
		this.pageViewRepository = pageViewRepository;
	}

	@Override
	public List<PageView> getPageViewListByCreateTime() {
		return this.pageViewRepository.getPageViewListByCreateTime();
	}

}
