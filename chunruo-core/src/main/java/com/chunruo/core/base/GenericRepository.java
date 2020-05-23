package com.chunruo.core.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface GenericRepository<T,ID extends Serializable> extends JpaRepository<T,ID>, PagingAndSortingRepository<T,ID>{
	
	public void detach(T object);
	
	public void clear(T object);
	
	public boolean contains(T object);
	
	public long count(String hql);
	
	public long count(String hql, Object[] params);
	
	public long countSql(String sql);
	
	public long countSql(String sql, Object[] params);
	
	public int executeSql(String sql);
	
	public int executeSql(String sql, Object[] params);
	
	public String executeSqlFunction(String sql);
	
	public String executeSqlFunction(String sql, Object[] params);
	
	public List<T> query(String hql);
	
	public List<T> query(String hql, Object[] params);
	
	public List<T> query(String hql, int start, int limit);
	
	public List<T> query(String hql, Object[] params, int start, int limit);
	
	public List<Object[]> querySql(String sql);
	
	public List<Object[]> querySql(String sql, Object[] params);
	
	public List<Object[]> querySql(String sql, int start, int limit);
	
	public List<Object[]> querySql(String sql, Object[] params, int start, int limit);
	
	public List<Map<String, Object>> querySqlMap(String sql);
	
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params);
	
	public List<Map<String, Object>> querySqlMap(String sql, int start, int limit);
	
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params, int start, int limit);
	
	public long countHql(Map<String, Object> paramMap);
	
	public long countHql(Map<String, Object> paramMap, Map<String, Object> paramOrMap);
	
	public List<T> getHqlPages(Map<String, Object> paramMap);
	
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap);
	
	public List<T> getHqlPages(Map<String, Object> paramMap, String sort, String dir);
	
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, String sort, String dir);
	
	public List<T> getHqlPages(Map<String, Object> paramMap, int start, int limit, String sort, String dir);
	
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, int start, int limit, String sort, String dir);
    
    public List<T> getByIdList(List<Long> idList);
    
    public void deleteByIdList(List<Long> idList);
    
    public List<T> batchInsert(List<T> modelList, int commitPerCount);

}