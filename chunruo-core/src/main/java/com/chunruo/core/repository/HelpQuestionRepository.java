package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.HelpQuestion;

@Repository("helpQuestionRepository")
public interface HelpQuestionRepository extends GenericRepository<HelpQuestion, Long> {

	@Query("from HelpQuestion where updateTime>:updateTime")
	List<HelpQuestion> getHelpQuestionListByUpdateTime(@Param("updateTime")Date updateTime);

}
