package com.chunruo.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chunruo.core.base.GenericRepository;
import com.chunruo.core.model.TagModel;

@Repository("tagModelRepository")
public interface TagModelRepository extends GenericRepository<TagModel, Long> {

	@Query("from TagModel where name =:name")
	public List<TagModel> getTagModelListByName(@Param("name")String name);
	
	@Query("from TagModel where objectId =:objectId and tagType =:tagType")
	public List<TagModel> getTagModelListByObjectId(@Param("objectId")Long objectId, @Param("tagType")Integer tagType);
	
	@Query("from TagModel where updateTime >:updateTime ")
	public List<TagModel> getTagModelListByUpdateTime(@Param("updateTime") Date updateTime);

	@Query("from TagModel where tagType =:tagType ")
	public List<TagModel> getTagModelListByTagType(@Param("tagType")Integer tagType);

	@Modifying
	@Query("update TagModel set isHotWord = :isHotWord,updateTime=now() where tagId in(:tagIdList)")
	public void updateTagModelisHotWord(@Param("tagIdList")List<Long> tagIdList,@Param("isHotWord") Boolean isHotWord);
}
