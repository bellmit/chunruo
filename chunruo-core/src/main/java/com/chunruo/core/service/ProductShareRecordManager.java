package com.chunruo.core.service;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductShareRecord;

public interface ProductShareRecordManager extends GenericManager<ProductShareRecord, Long>{

	public ProductShareRecord getProductShareRecordByToken(String token);
	
	public String getToken();

}
