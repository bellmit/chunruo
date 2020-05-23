package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.WeiNiProduct;

@Repository("weiNiProductRepository")
public interface WeiNiProductRepository extends GenericRepository<WeiNiProduct, Long> {

}
