package com.chunruo.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Identification;

@Repository("identificationRepository")
public interface IdentificationRepository extends GenericRepository<Identification, Long> {

	@Query("from Identification where idCardNo=:idCardNo")
	public List<Identification> getIdentificationListByIdCardNo(@Param("idCardNo") String idCardNo);
	
	@Query("from Identification where upper(idCardNo) in (:idCardNoList)")
	public List<Identification> getListByIdCardNoList(@Param("idCardNoList")List<String> idCardNoList);

	@Modifying
	@Query("update Identification set status=:status,updateTime=now() where idCardNo=:idCardNo")
	public void updateIdentificationByStatus(@Param("status")Boolean status,@Param("idCardNo") String idCardNo);
}
