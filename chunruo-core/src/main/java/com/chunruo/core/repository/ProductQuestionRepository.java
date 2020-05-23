package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.ProductQuestion;

/**
 * 商品疑问
 * @author chunruo
 *
 */
@Repository("productQuestionRepository")
public interface ProductQuestionRepository extends GenericRepository<ProductQuestion, Long> {

	@Query("from ProductQuestion where productId =:productId and isDelete = false")
	public List<ProductQuestion> getQuestionListByProductId(@Param("productId")Long productId);

	@Query("from ProductQuestion where updateTime >:updateTime")
	public List<ProductQuestion> getQuestionListByUpdateTime(@Param("updateTime")Date updateTime);
	
	@Modifying
	@Query("update ProductQuestion set status =:status, updateTime = now() where questionId in (:idList)")
	public void updateQuestionStatusByIdList(@Param("idList")List<Long>idList, @Param("status")Boolean status);
	
	@Modifying
	@Query("update ProductQuestion set isDelete =:isDelete, updateTime = now() where questionId in (:idList)")
	public void deleteQuestionByIdList(@Param("idList")List<Long>idList, @Param("isDelete")Boolean isDelete);
}

