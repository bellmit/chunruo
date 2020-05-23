package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductAnswer;

/**
 * 商品疑问回答
 * @author chunruo
 *
 */
@Repository("productAnswerRepository")
public interface ProductAnswerRepository extends GenericRepository<ProductAnswer, Long> {

	@Query("from ProductAnswer where questionId =:questionId and isDelete = false")
	public List<ProductAnswer> getAnswerListByQuestionId(@Param("questionId")Long questionId);

	@Query("from ProductAnswer where updateTime >:updateTime")
	public List<ProductAnswer> getAnswerListByUpdateTime(@Param("updateTime") Date updateTime);
	
	@Modifying
	@Query("update ProductAnswer set status =:status, updateTime = now() where answerId in (:idList)")
	public void updateAnswerStatusByIdList(@Param("idList") List<Long>idList , @Param("status") Boolean status);
	
	@Modifying
	@Query("update ProductAnswer set isDelete =:isDelete, updateTime = now() where answerId in (:idList)")
	public void deleteAnswerByIdList(@Param("idList") List<Long>idList ,@Param("isDelete")  Boolean isDelete);
	
	@Modifying
	@Query("update ProductAnswer set isDelete =:isDelete, updateTime = now() where questionId in (:idList)")
	public void deleteAnswerByQuestionIdList(@Param("idList") List<Long>idList , @Param("isDelete") Boolean isDelete);
}
