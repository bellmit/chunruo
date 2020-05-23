package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserSociety;

@Repository("userSocietyRepository")
public interface UserSocietyRepository extends GenericRepository<UserSociety, Long> {

	@Query("from UserSociety where appConfigId =:appConfigId and openId =:openId")
	public UserSociety getUserSocietyByOpenIdAndConfigId(@Param("appConfigId")Long appConfigId, @Param("openId")String openId);
	
	@Query("from UserSociety where unionId =:unionId")
	public List<UserSociety> getUserSocietyByUnionId(@Param("unionId")String unionId);
	
	@Modifying
	@Query("delete from UserSociety where openId in(:openidList)")
	public void deleteUserSocietyByOpenId(@Param("openidList")List<String> openidList);
	
	@Modifying
	@Query("delete from UserSociety where unionId =:unionId")
	public void deleteUserSocietyByUnionId(@Param("unionId")String unionId);

	@Query("from UserSociety where unionId =:unionId and appConfigId=:appConfigId")
	public List<UserSociety> getUserSocietyByUniodIdAndAppConfigId(@Param("unionId")String unionId, @Param("appConfigId")Long appConfigId);
}
