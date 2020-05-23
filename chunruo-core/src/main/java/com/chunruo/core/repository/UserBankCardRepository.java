package com.chunruo.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserBankCard;

@Repository("userBankCardRepository")
public interface UserBankCardRepository extends GenericRepository<UserBankCard, Long> {

	@Query("from UserBankCard where userId =:userId")
	public List<UserBankCard> getListByUserId(@Param("userId")Long userId);
	
	@Query("from UserBankCard where userId =:userId and bankCardNo =:bankCardNo")
	public UserBankCard getByUserIdAndBankCardNo(@Param("userId")Long userId, @Param("bankCardNo")String bankCardNo);
}
