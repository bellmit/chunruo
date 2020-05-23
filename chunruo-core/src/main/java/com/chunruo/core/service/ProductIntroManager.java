package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductIntro;

public interface ProductIntroManager extends GenericManager<ProductIntro, Long>{

	List<ProductIntro> getProductIntroListByUpdateTime(Date updateTime);

}
