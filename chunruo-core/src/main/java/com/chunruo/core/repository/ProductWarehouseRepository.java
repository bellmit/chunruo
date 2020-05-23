package com.chunruo.core.repository;


import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductWarehouse;

@Repository("productWarehouseRepository")
public interface ProductWarehouseRepository extends GenericRepository<ProductWarehouse, Long> {

}
