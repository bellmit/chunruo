package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductGift;

@Repository("productGiftRepository")
public interface ProductGiftRepository extends GenericRepository<ProductGift, Long> {


}
