package com.chunruo.core.util;

public class SplitPageUtil {

	/**
	 * 得到分页总页码
	 * @param rowTotal	总记录数
	 * @param pageRowNum 每页记录
	 * @return	总页码
	 */
	public static long getSplitPageMax(long rowTotal,long pageRowNum){
		long pageMax=1;
		pageRowNum = pageRowNum < 1 ? 1 : pageRowNum;
		if (rowTotal >= 0) {
			pageMax = rowTotal / pageRowNum;// 总共多少页
			if (pageMax == 0)
				pageMax += 1;// 因为第一页总是有的
			if (rowTotal % pageRowNum != 0 && rowTotal > pageRowNum)
				pageMax += 1;// 有余数时,应增加一页
		}
		return pageMax;
	}
	/**
	 * 当前页第一条记录
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	public static long getStart(long nowPage,long pageSize){
		long start = (nowPage-1)*pageSize+1;
		return start;
	}
	/**
	 * 当前页最后一条记录
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	public static long getLimit(long nowPage,long pageSize){
		long limit = getStart(nowPage,pageSize)+pageSize-1;
		return limit;
	}
	
	/**
	 * 得到最大页数 数据类型为int
	 * 理由 ： 容器类型的长度和索引均为int 省去操作时的类型转换 
	 * @param rowTotal
	 * @param pageSize
	 * @return
	 */
	public static int getMaxPage(int rowTotal,int pageSize){
		int pageMax=0;
		pageSize = pageSize < 1 ? 1 : pageSize;
		if (rowTotal >= 0) {
			if(rowTotal % pageSize ==0){
				pageMax = rowTotal/pageSize;
			}else{
				pageMax = rowTotal/pageSize + 1;
			}
		}
		return pageMax;
	}
	
	/**
	 * 
	 * 缓存中找内容 从0开始
	 * 得到当前页开始记录数
	 * 数据类型int 省去 使用容器类时候的类型转换
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	public static int getStart(int nowPage,int pageSize){
		int start = (nowPage-1)*pageSize;
		return start;
	}
	
	/**
	 * 当前页最后一条记录
	 * 数据类型int 省去 使用容器类时候的类型转换
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	public static int getEnd(int nowPage,int pageSize){
		int limit = getStart(nowPage,pageSize)+pageSize-1;
		return limit;
	}
}
