package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserLevelExplain;

public interface UserLevelExplainManager extends GenericManager<UserLevelExplain, Long> {

	List<UserLevelExplain> getUserLevelExplainList(Integer level, List<Integer> typeList);

}
