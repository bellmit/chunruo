package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.PageView;

public interface PageViewManager extends GenericManager<PageView, Long> {

	List<PageView> getPageViewListByCreateTime();

}
