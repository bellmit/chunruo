package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.ProductWarehouseTemplate;

public interface ProductWarehouseTemplateManager extends GenericManager<ProductWarehouseTemplate, Long> {

	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByStatus(boolean status);

	public List<ProductWarehouseTemplate> getProductWarehouseTemplateListByUpdateTime(Date updateTime);

	public ProductWarehouseTemplate getMemberYearsTemplateByName(String name);

}
