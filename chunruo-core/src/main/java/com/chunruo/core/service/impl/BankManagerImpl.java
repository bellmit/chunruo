package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Bank;
import com.chunruo.core.repository.BankRepository;
import com.chunruo.core.service.BankManager;

@Transactional
@Component("bankManager")
public class BankManagerImpl extends GenericManagerImpl<Bank, Long> implements BankManager{
	private BankRepository bankRepository;

	@Autowired
	public BankManagerImpl(BankRepository bankRepository) {
		super(bankRepository);
		this.bankRepository = bankRepository;
	}

	@Override
	public List<Bank> getBankListByStatus(boolean status) {
		return this.bankRepository.getBankListByStatus(status);
	}
	
	@Override
	public Bank getBankByBankId(Long bankId) {
		return this.bankRepository.getBankByBankId(bankId);
	}
}
