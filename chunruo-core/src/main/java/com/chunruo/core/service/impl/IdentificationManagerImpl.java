package com.chunruo.core.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Identification;
import com.chunruo.core.repository.IdentificationRepository;
import com.chunruo.core.service.IdentificationManager;

@Transactional
@Component("identificationManager")
public class IdentificationManagerImpl extends GenericManagerImpl<Identification, Long> implements IdentificationManager{
	private IdentificationRepository identificationRepository;

	@Autowired
	public IdentificationManagerImpl(IdentificationRepository identificationRepository) {
		super(identificationRepository);
		this.identificationRepository = identificationRepository;
	}

	@Override
	public Identification getIdentificationByIdCardNo(String idCardNo) {
		List<Identification> list = this.identificationRepository.getIdentificationListByIdCardNo(idCardNo);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public void updateIdentificationByStatus(boolean status,String idCardNo) {
		this.identificationRepository.updateIdentificationByStatus(status,idCardNo);
	}

	@Override
	public List<Identification> getListByIdCardNoList(List<String> idCardNoList) {
		if(idCardNoList != null && idCardNoList.size() > 0) {
			return this.identificationRepository.getListByIdCardNoList(idCardNoList);
		}
		return null;
	}
}
