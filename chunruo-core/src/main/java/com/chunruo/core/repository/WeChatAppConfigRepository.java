package com.chunruo.core.repository;

import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.WeChatAppConfig;

@Repository("weChatAppConfigRepository")
public interface WeChatAppConfigRepository extends GenericRepository<WeChatAppConfig, Long> {

}
