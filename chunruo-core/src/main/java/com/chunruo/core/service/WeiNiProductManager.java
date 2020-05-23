package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.WeiNiProduct;

public interface WeiNiProductManager extends GenericManager<WeiNiProduct, Long> {

	public boolean batchInsertWeiNiProduct(List<WeiNiProduct> modelList, int commitPerCount);
}
