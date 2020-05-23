package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Bank;

@Repository("bankRepository")
public interface BankRepository extends GenericRepository<Bank, Long> {

	@Query("from Bank u where u.status=:status order by bankId desc")
    public List<Bank> getBankListByStatus(@Param("status") boolean status);
	
	@Query("from Bank u where u.bankId=:bankId")
	public Bank getBankByBankId(@Param("bankId") Long bankId);
}
