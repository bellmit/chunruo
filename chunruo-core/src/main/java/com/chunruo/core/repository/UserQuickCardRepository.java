package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserQuickCard;

@Repository("userQuickCardRepository")
public interface UserQuickCardRepository extends GenericRepository<UserQuickCard, Long>{

	@Query("from UserQuickCard where userId =:userId")
	public List<UserQuickCard> getUserQuickCardListByUserId(@Param("userId")Long userId);
	
	@Query("from UserQuickCard where bankCardNumber =:bankCardNumber")
	public List<UserQuickCard> getUserQuickCardListByBankCardNumber(@Param("bankCardNumber")String bankCardNumber);
	
	@Query("from UserQuickCard where userId =:userId and bankCardNumber =:bankCardNumber")
	public List<UserQuickCard> getUserQuickCardListByUserId(@Param("userId")Long userId,@Param("bankCardNumber")String bankCardNumber);

}
