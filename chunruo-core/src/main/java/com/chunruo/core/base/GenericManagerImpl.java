package com.chunruo.core.base;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GenericManagerImpl<T, PK extends Serializable> implements GenericManager<T, PK> {
	protected final Log log = LogFactory.getLog(getClass());
	protected GenericRepository<T, PK> genericRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public GenericManagerImpl(final GenericRepository<T, PK> genericRepository) {
		this.genericRepository = genericRepository;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	@Override
	public T detach(T object) {
		try{
			this.genericRepository.detach(object);
		}catch(Exception e){
		}
		return object;
	}
	
	@Override
	public List<T> detach(List<T> list) {
		if(list != null && list.size() > 0){
			for(T object : list){
				try{
					this.genericRepository.detach(object);
				}catch(Exception e){
					continue;
				}
			}
		}
		return list;
	}

	@Override
	public boolean contains(T object) {
		return this.genericRepository.contains(object);
	} 

	@Override
	public boolean exists(PK id){
		return this.genericRepository.exists(id);
	}
	
	@Override
	public T save(T object){
		return this.genericRepository.save(object);
	}
	
	@Override
	public T update(T object){
		return this.genericRepository.saveAndFlush(object);
	}
	
	@Override
	public T get(PK id){
		return this.genericRepository.findOne(id);
	}
	
	@Override
	public List<T> getAll(){
		return this.genericRepository.findAll();
	}
	
	@Override
	public void remove(PK id){
		this.genericRepository.delete(id);
	}
	
	@Override
	public void remove(T object){
		this.genericRepository.delete(object);
	}
	
	@Override
	public long count(String hql) {
		return this.genericRepository.count(hql);
	}

	@Override
	public long count(String hql, Object[] params) {
		return this.genericRepository.count(hql, params);
	}
	
	@Override
	public long countSql(String sql){
		return this.genericRepository.countSql(sql);
	}
	
	@Override
	public long countSql(String sql, Object[] params){
		return this.genericRepository.countSql(sql, params);
	}
	
	@Override
	public int executeSql(String sql){
		return this.genericRepository.executeSql(sql);
	}
	
	@Override
	public int executeSql(String sql, Object[] params){
		return this.genericRepository.executeSql(sql, params);
	}

	@Override
	public List<T> query(String hql) {
		return this.genericRepository.query(hql);
	}

	@Override
	public List<T> query(String hql, Object[] params) {
		return this.genericRepository.query(hql, params);
	}

	@Override
	public List<T> query(String hql, int start, int limit) {
		return this.genericRepository.query(hql, start, limit);
	}

	@Override
	public List<T> query(String hql, Object[] params, int start, int limit) {
		return this.genericRepository.query(hql, params, start, limit);
	}
	
	@Override
	public List<Object[]> querySql(String sql){
		return this.genericRepository.querySql(sql);
	}
	
	@Override
	public List<Object[]> querySql(String sql, Object[] params){
		return this.genericRepository.querySql(sql, params);
	}
	
	@Override
	public List<Object[]> querySql(String sql, int start, int limit){
		return this.genericRepository.querySql(sql, start, limit);
	}
	
	@Override
	public List<Object[]> querySql(String sql, Object[] params, int start, int limit){
		return this.genericRepository.querySql(sql, params, start, limit);
	}
	
	@Override
	public List<Map<String, Object>> querySqlMap(String sql){
		return this.genericRepository.querySqlMap(sql);
	}
	
	@Override
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params){
		return this.genericRepository.querySqlMap(sql, params);
	}
	
	@Override
	public List<Map<String, Object>> querySqlMap(String sql, int start, int limit){
		return this.genericRepository.querySqlMap(sql, start, limit);
	}
	
	@Override
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params, int start, int limit){
		return this.genericRepository.querySqlMap(sql, params, start, limit);
	}

	@Override
	public long countHql(Map<String, Object> paramMap) {
		return this.genericRepository.countHql(paramMap);
	}

	@Override
	public long countHql(Map<String, Object> paramMap, Map<String, Object> paramOrMap) {
		return this.genericRepository.countHql(paramMap, paramOrMap);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap) {
		return this.genericRepository.getHqlPages(paramMap);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap) {
		return this.genericRepository.getHqlPages(paramMap, paramOrMap);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap, String sort, String dir) {
		return this.genericRepository.getHqlPages(paramMap, sort, dir);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, String sort, String dir) {
		return this.genericRepository.getHqlPages(paramMap, paramOrMap, sort, dir);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap, int start, int limit, String sort, String dir) {
		return this.genericRepository.getHqlPages(paramMap, start, limit, sort, dir);
	}

	@Override
	public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, int start, int limit, String sort, String dir) {
		return this.genericRepository.getHqlPages(paramMap, paramOrMap, start, limit, sort, dir);
	}

	@Override
	public List<T> getByIdList(List<Long> idList) {
		if(idList != null && idList.size() > 0){
			return this.genericRepository.getByIdList(idList);
		}
		return null;
	}

	@Override
	public void deleteByIdList(List<Long> idList) {
		if(idList != null && idList.size() > 0){
			this.genericRepository.deleteByIdList(idList);
		}
	}
	
	@Override
	public List<T> batchInsert(List<T> modelList, int commitPerCount){
		return this.genericRepository.batchInsert(modelList, commitPerCount);
	}
	
	@Transactional
	@Override
	public int[] batchUpdate(String sql, final List<Object[]> dataSet){
		if(dataSet != null && dataSet.size() > 0){
			BatchPreparedStatementSetter setter=new BatchPreparedStatementSetter(){
				public int getBatchSize(){
					return dataSet.size();
				}
				
				public void setValues(PreparedStatement ps,int index){
					try{
						Object[] obj = dataSet.get(index);
						for(int i = 0; i < obj.length; i ++){
							ps.setObject(i + 1, obj[i]);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
			return this.jdbcTemplate.batchUpdate(sql, setter);
		}
		return null;
	}

	
}
