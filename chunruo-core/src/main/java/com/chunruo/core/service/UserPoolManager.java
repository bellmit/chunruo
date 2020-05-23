package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserPool;

public interface UserPoolManager extends GenericManager<UserPool, Long> {

	public UserPool getUserPoolByUserId(Long userId);

	public List<UserPool> getUserPoolListByType(Integer type,String keyword,Integer status);

	public List<UserPool> getUserPoolListByUserIdListAndIsBindBdUser(List<Long> userIdList,Boolean isBindBdUser);

	public List<UserPool> getUserPoolListByUserIdList(List<Long> userIdList);

	public void deleteUserPoolByUserIdList(List<Long> userIdList);

}
