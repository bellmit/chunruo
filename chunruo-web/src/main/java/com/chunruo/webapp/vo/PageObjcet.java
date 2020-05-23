package com.chunruo.webapp.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chunruo.core.util.StringUtil;

public class PageObjcet <T extends Serializable>{
	public final static String SORE_ASC ="ASC";
	public final static String SORE_DESC = "DESC";
	public final static  Long DEFUALT_LIMIT = 10L;
	public final static Integer INXEX_MAX = 5;
	private List<T> pageList;
	private Long total;//总数
	//	总页数
	private Integer totalPages;
	
	private Long limit ;
	
	private Integer currentPage;
	
	private Integer start;
	
	private String queryString;
	
	//	分页下标列表
	private List<Integer> pageIndexList; 
	
	public List<T> getPageList() {
		return pageList;
	}
	
	public void setPageList(List<T> pageList) {
		this.pageList = pageList;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
		this.totalPages = total.intValue() / limit.intValue();
		if (total.intValue() % limit.intValue() > 0)
			this.totalPages ++;
		if (start.intValue() > total.intValue()){
			currentPage = totalPages;
		}
	}
	public Integer getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(Integer totalPages) {
		
		this.totalPages = totalPages;
		
	}
	public Long getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		if (StringUtil.isNull(limit) || StringUtil.compareObject(limit, 0)){
			limit = DEFUALT_LIMIT;
		}
		this.limit = limit;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		if (StringUtil.isNull(currentPage) || StringUtil.compareObject(0, currentPage))
			currentPage = 1;
		this.currentPage = currentPage;
	}
	public Integer getStart() {
		return start = limit.intValue() * (currentPage - 1);
//		
	}
	public void setStart() {
		this.start = limit.intValue() * (currentPage - 1);
	}

	public List<Integer> getPageIndexList() {
		return pageIndexList;
	}

	public void setPageIndexList() {
		
		pageIndexList = new ArrayList<Integer>();
		if (totalPages.intValue() < INXEX_MAX){
			for (int i=1 ; i<=totalPages ;i++){
				pageIndexList.add(i);
			}
		}else{
			int indexFlag =1;
			if (currentPage.intValue() - indexFlag > INXEX_MAX/2){
				indexFlag = currentPage.intValue()- INXEX_MAX/2;
				
				if (totalPages.intValue() - currentPage.intValue() < INXEX_MAX/2){
					indexFlag = indexFlag - (INXEX_MAX/2 - totalPages.intValue() + currentPage.intValue());
				}
				
			}
			for (int i=0;i<INXEX_MAX;i++){
				pageIndexList.add(i + indexFlag);
			}
		}
	}
//	public static void main(String[] args) {
//		PageObjcet page = new PageObjcet(20L, 2L);
//		page.setCurrentPage(10);
//		page.setPageIndexList();
//		
//		System.out.println(StringUtil.objectToJSON(page.getPageIndexList()));
//	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
}
