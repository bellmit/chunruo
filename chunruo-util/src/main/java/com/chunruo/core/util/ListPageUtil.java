package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.List;

import com.chunruo.core.util.vo.ListPageVo;

/**
 * List集合分页工具
 * @author chunruo
 * @param <T>
 */
public abstract class ListPageUtil<T> {
	
	/**
	 * 自定义添加对象
	 * 如果返回为空不添加到集合中
	 * @param objectId
	 * @return
	 */
	public abstract T addObject(Long objectId);

	/**
	 * List集合分页
	 * @param idList
	 * @param lastId
	 * @param pageidx
	 * @param pagesize
	 * @return
	 */
	public ListPageVo<List<T>> getPageList(List<Long> idList, Long lastId, int pageidx, int pagesize){
		Long currentLastId = 0L;
		boolean isNextPageURL = false;
		List<T> resultList = new ArrayList<T> ();
		if (lastId == null || StringUtil.compareObject(lastId, 0)){
			lastId = null;
		}
		ListPageVo<List<T>> listPageVo = new ListPageVo<List<T>> ();
		if(idList == null || idList.size() == 0){
			listPageVo.setCount(0);
			listPageVo.setDataList(resultList);
			listPageVo.setLastId(currentLastId);
			listPageVo.setIsNextPageURL(isNextPageURL);
			return listPageVo;
		}
		
		// 分页
		if(lastId != null  && idList.contains(lastId)){
			// 根据上次分页最后lastId实现分页
			List<Long> lastResultList = new ArrayList<Long> ();
			boolean isFindIdPosition = false;
			for(Long productId : idList){
				if(!isFindIdPosition){
					if(StringUtil.compareObject(lastId, productId)){
						isFindIdPosition = true;
					}
					continue;
				}
				lastResultList.add(productId);
			}

			// 动态识别是否有分页码
			if(lastResultList != null && lastResultList.size() > 0){
				if(lastResultList.size() <= pagesize){
					// 全量拷贝
					for(Long objectId : lastResultList){
						T object = this.addObject(objectId);
						if(object != null){
							resultList.add(object);
						}
					}
				}else{
					// 局部拷贝|考虑分页码
					for(int i = 0; i < pagesize; i ++ ){
						Long objectId = lastResultList.get(i);
						T object = this.addObject(objectId);
						if(object != null){
							resultList.add(object);
						}
						currentLastId = objectId;
					}
					isNextPageURL = true;
				}
			}
		}else{
			long pageMax = SplitPageUtil.getSplitPageMax(idList.size(), pagesize);
			long start = SplitPageUtil.getStart(pageidx, pagesize);
			long limit = SplitPageUtil.getEnd(pageidx, pagesize);
		
			for (int i = 0; i < idList.size(); i++) {
				if (i >= start && i <= limit) {
					Long objectId = idList.get(i);
					T object = this.addObject(objectId);
					if(object != null){
						resultList.add(object);
					}
					currentLastId = objectId;
				} else if (i > limit) {
					break;
				}
			}

			// 是否有分页
			if(pageMax > 1 && (pageidx+1) <= pageMax){
				isNextPageURL = true;
			}
			listPageVo.setPageMax(pageMax);
		}
		listPageVo.setCount(idList.size());
		listPageVo.setPageidx(pageidx);
		listPageVo.setDataList(resultList);
		listPageVo.setLastId(currentLastId);
		listPageVo.setIsNextPageURL(isNextPageURL);
		return listPageVo;
	}
}
