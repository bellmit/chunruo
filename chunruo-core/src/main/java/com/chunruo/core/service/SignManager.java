package com.chunruo.core.service;


import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Sign;

public interface SignManager extends GenericManager<Sign, Long>{

	Sign getSignByUserId(Long userId);

	List<Sign> getSignListByUpdateTime(Date updateTime);

}
