package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.Feedback;

@Repository("feedbackRepository")
public interface FeedbackRepository extends GenericRepository<Feedback, Long> {
	
	@Query("from Feedback where userId=:userId")
	List<Feedback> getFeedbackListByUserId(@Param("userId")Long userId);

	@Query("from Feedback where updateTime>:updateTime")
	List<Feedback> getFeedbackListByUpdateTime(@Param("updateTime")Date updateTime);
	
}
