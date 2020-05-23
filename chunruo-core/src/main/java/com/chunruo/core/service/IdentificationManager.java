package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Identification;

public interface IdentificationManager extends GenericManager<Identification, Long>{

	public Identification getIdentificationByIdCardNo(String idCardNo);

	public List<Identification> getListByIdCardNoList(List<String> idCardNoList);
	
	public void updateIdentificationByStatus(boolean status,String idCardNo);
}
