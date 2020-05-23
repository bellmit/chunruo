package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Bank;

public interface BankManager extends GenericManager<Bank, Long> {
	public List<Bank> getBankListByStatus(boolean status);

	Bank getBankByBankId(Long bankId);

}
