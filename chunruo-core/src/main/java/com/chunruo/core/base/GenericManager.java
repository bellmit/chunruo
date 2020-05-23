package com.chunruo.core.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface GenericManager<T, PK extends Serializable> {
	
	public T detach(T object);
	
	public List<T> detach(List<T> list);
	
	public boolean contains(T object);
	
	public boolean exists(PK id);
	
	public T save(T object);
	
	public T update(T object);
	
	public T get(PK id);
	
	public List<T> getAll();
	
	public void remove(T object);
	
	public void remove(PK id);

	public long count(String hql);
	
	public long count(String hql, Object[] params);
	
	public long countSql(String sql);
	
	public long countSql(String sql, Object[] params);
	
	public int executeSql(String sql);
	
	public int executeSql(String sql, Object[] params);
	
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
    
    public int[] batchUpdate(String sql, List<Object[]> dataSet);
}
