package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.UserInviteRecord;

@Repository("userInviteRecordRepository")
public interface UserInviteRecordRepository extends GenericRepository<UserInviteRecord, Long> {
	
	@Query("from UserInviteRecord where recordNo=:recordNo")
	public List<UserInviteRecord> getUserInviteRecordByRecordNo(@Param("recordNo") String recordNo);
	
	@Query("from UserInviteRecord where userId=:userId")
	public List<UserInviteRecord> getUserInviteRecordByUserId(@Param("userId") Long userId);
	
	@Query("from UserInviteRecord where topUserId=:topUserId and invitationType=:inviteType and isValidInvitNumber = true order by recordId")
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(@Param("topUserId") Long topUserId, @Param("inviteType")Integer inviteType);
	
	@Query("from UserInviteRecord where topUserId=:topUserId and inviteType=:inviteType and (createTime BETWEEN :beginDate and :endDate)  order by recordId")
	public List<UserInviteRecord> getValidInvitNumberByTopUserId(@Param("topUserId") Long topUserId, @Param("inviteType")Integer inviteType, @Param("beginDate")Date beginDate, @Param("endDate")Date endDate);
	
	@Query("from UserInviteRecord where (createTime BETWEEN :beginDate and :endDate)  and isPaymentSucc=true order by recordId")
	public List<UserInviteRecord> getUserInviteRecordListByCreateTime(@Param("beginDate")Date beginDate, @Param("endDate")Date endDate);
	
	@Query("from UserInviteRecord where isPaymentSucc=true AND  DATE_FORMAT( createTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) order by createTime desc")
	public List<UserInviteRecord> getUserInviteRecordListByCurrentMonth();

	@Query("from UserInviteRecord where isPaymentSucc=true order by createTime desc")
	public List<UserInviteRecord> getUserInviteRecordList();

	@Query("from UserInviteRecord where isPaymentSucc=true and userId in(:userIdList) order by createTime desc")
	public List<UserInviteRecord> getUserInviteRecordListByUserIdList(@Param("userIdList")List<Long> userIdList);

}
