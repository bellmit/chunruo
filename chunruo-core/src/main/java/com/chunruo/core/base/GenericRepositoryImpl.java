package com.chunruo.core.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.chunruo.core.util.StringUtil;

@SuppressWarnings("unchecked")
public class GenericRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T,ID> implements GenericRepository<T,ID> {
	public static final int QUERY_PAGE_MAX_SIZE = 2000; 
	public static final SimpleDateFormat DATE_PATTERN = new SimpleDateFormat("yyyy-MM-dd");
	private SQLBuilder sqlBuilder = new SQLBuilder();
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    //父类没有不带参数的构造方法，这里手动构造父类
    public GenericRepositoryImpl(Class<T> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

	@Override
    public void detach(T object) {
    	this.entityManager.detach(object);
    }
    
    @Override
	public void clear(T object) {
	  this.entityManager.clear();
		
	}
    
    @Override
	public boolean contains(Object object) {
		return this.entityManager.contains(object);
	}
    
    @Override
    public long count(String hql){
    	Query query = this.entityManager.createQuery(hql);
		long ret = ((Long) query.getSingleResult()).longValue();
		return ret;
    }
    
    @Override
    public long count(String hql, Object[] params){
    	Query query = this.entityManager.createQuery(hql);
    	for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		long ret = ((Long) query.getSingleResult()).longValue();
		return ret;
    }
    
    @Override
    public long countSql(String sql){
    	Query query = this.entityManager.createNativeQuery(sql);
		
		Long ret = 0L;
		Object un = query.getSingleResult();
		if (un instanceof BigDecimal) {
			ret = ((BigDecimal) un).longValue();
		} else if (un instanceof BigInteger) {
			ret = ((BigInteger) un).longValue();
		}
		this.entityManager.close();
		return ret;
    }
	
    @Override
	public long countSql(String sql, Object[] params){
    	Query query = this.entityManager.createNativeQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		
		Long ret = 0L;
		Object un = query.getSingleResult();
		if (un instanceof BigDecimal) {
			ret = ((BigDecimal) un).longValue();
		} else if (un instanceof BigInteger) {
			ret = ((BigInteger) un).longValue();
		}
		this.entityManager.close();
		return ret;
    }
    
    @Override
    @Transactional
    public int executeSql(String sql){
    	Query query = this.entityManager.createNativeQuery(sql);
    	int result = query.executeUpdate();
    	this.entityManager.close();
    	return result;
    }
    
    @Override
    @Transactional
    public String executeSqlFunction(String sql){
    	Query query = this.entityManager.createNativeQuery(sql);
    	String result = StringUtil.null2Str(query.getSingleResult());
    	this.entityManager.close();
    	return result;
    }
    
    @Override
    @Transactional
    public String executeSqlFunction(String sql, Object[] params){
    	Query query = this.entityManager.createNativeQuery(sql);
    	for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
    	
    	String result = StringUtil.null2Str(query.getSingleResult());
    	this.entityManager.close();
    	return result;
    }
	
    @Override
    @Transactional
	public int executeSql(String sql, Object[] params){
    	Query query = this.entityManager.createNativeQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		
		int result = query.executeUpdate();
		this.entityManager.close();
    	return result;
    }
    
    @Override
    public List<T> query(String hql){
    	Query query = this.entityManager.createQuery(hql);
		return query.getResultList();
    }
    
    @Override
    public List<T> query(String hql, Object[] params){
    	Query query = this.entityManager.createQuery(hql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		List<T> ls = query.getResultList();
		return ls;
    }
    
    @Override
    public List<T> query(String hql, int start, int limit) {
		Query query = this.entityManager.createQuery(hql);
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		return query.getResultList();
    }
    
    @Override
    public List<T> query(String hql, Object[] params, int start, int limit){
    	Query query = this.entityManager.createQuery(hql);
    	for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		return query.getResultList();
    }
    
    @Override
    public List<Object[]> querySql(String sql){
    	Query query = this.entityManager.createNativeQuery(sql);
    	List<Object[]> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Object[]> querySql(String sql, Object[] params){
    	Query query = this.entityManager.createNativeQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		
		List<Object[]> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Object[]> querySql(String sql, int start, int limit){
    	Query query = this.entityManager.createNativeQuery(sql);
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		
		List<Object[]> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Object[]> querySql(String sql, Object[] params, int start, int limit){
    	Query query = this.entityManager.createNativeQuery(sql);
    	for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		
		List<Object[]> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
	@Override
    public List<Map<String, Object>> querySqlMap(String sql){
    	Query query = this.entityManager.createNativeQuery(sql);
    	query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	List<Map<String, Object>> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params){
    	Query query = this.entityManager.createNativeQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Map<String, Object>> querySqlMap(String sql, int start, int limit){
    	Query query = this.entityManager.createNativeQuery(sql);
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
	
    @Override
	public List<Map<String, Object>> querySqlMap(String sql, Object[] params, int start, int limit){
    	Query query = this.entityManager.createNativeQuery(sql);
    	for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		
		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.getResultList();
		this.entityManager.close();
    	return result;
    }
    
    @Override
    public long countHql(Map<String, Object> paramMap){
    	Long ret = 0L;
    	String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		StringBuffer hql = new StringBuffer(String.format("select count(o.%s) from %s o ", pkName, entityName));
		if (paramMap != null && paramMap.size() > 0) {
			hql.append(this.getHqlParamKey(paramMap, null));
			
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		this.setHqlParamValue(query, paramMap);
		ret = ((Long) query.getSingleResult()).longValue();
		entityManager.flush();
		return ret;
    }
	
    @Override
	public long countHql(Map<String, Object> paramMap, Map<String, Object> paramOrMap){
    	Long ret = 0L;
    	String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		StringBuffer hql = new StringBuffer(String.format("select count(o.%s) from %s o ", pkName, entityName));
		if ((paramMap != null && paramMap.size() > 0) || (paramOrMap != null && paramOrMap.size() > 0)) {
			hql.append(this.getHqlParamKey(paramMap, paramOrMap));
			
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		this.setHqlParamValue(query, paramMap);
		this.setHqlParamValue(query, paramOrMap);
		ret = ((Long) query.getSingleResult()).longValue();
		entityManager.flush();
		return ret;
    }
    
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap){
    	return this.getHqlPages(paramMap, null, null);
    }
    
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap){
    	return this.getHqlPages(paramMap, paramOrMap, null, null);
    }
    
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap, String sort, String dir){
    	String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		StringBuffer hql = new StringBuffer(String.format("from %s o ", entityName));
		if(paramMap != null && paramMap.size() > 0){
			hql.append(this.getHqlParamKey(paramMap, null));
		}
		
		if(!StringUtil.isNull(sort)){
			hql.append(" order by o." + sort + " " + StringUtil.null2Str(dir));
		}else{
			hql.append(String.format(" order by o.%s desc ", pkName));
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		this.setHqlParamValue(query, paramMap);
		List<T> list = query.getResultList();
		entityManager.flush();
		return list;
    }
    
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, String sort, String dir){
    	String entityName = sqlBuilder.getEntityName(entityClass);
    	String pkName = sqlBuilder.getPkField(entityClass);
		StringBuffer hql = new StringBuffer(String.format("from %s o", entityName));
		if ((paramMap != null && paramMap.size() > 0) || (paramOrMap != null && paramOrMap.size() > 0)) {
			hql.append(this.getHqlParamKey(paramMap, paramOrMap));
		}
		
		if(!StringUtil.isNull(sort)){
			hql.append(" order by o." + sort + " " + StringUtil.null2Str(dir));
		}else{
			hql.append(String.format(" order by o.%s desc ", pkName));
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		this.setHqlParamValue(query, paramMap);
		this.setHqlParamValue(query, paramOrMap);
		List<T> list = query.getResultList();
		entityManager.flush();
		return list;
    }
    
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap, int start, int limit, String sort, String dir){
    	String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		StringBuffer hql = new StringBuffer(String.format("from %s o ", entityName));
		if(paramMap != null && paramMap.size() > 0){
			hql.append(this.getHqlParamKey(paramMap, null));
		}
		
		if(!StringUtil.isNull(sort)){
			hql.append(" order by o." + sort + " " + StringUtil.null2Str(dir));
		}else{
			hql.append(String.format(" order by o.%s desc ", pkName));
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		this.setHqlParamValue(query, paramMap);
		List<T> list = query.getResultList();
		entityManager.flush();
		return list;
    }
	
    @Override
    public List<T> getHqlPages(Map<String, Object> paramMap, Map<String, Object> paramOrMap, int start, int limit, String sort, String dir){
    	String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
    	StringBuffer hql = new StringBuffer(String.format("from %s o", entityName));
		if ((paramMap != null && paramMap.size() > 0) || (paramOrMap != null && paramOrMap.size() > 0)) {
			hql.append(this.getHqlParamKey(paramMap, paramOrMap));
		}
		
		// sort
		if(!StringUtil.isNull(sort)){
			hql.append(" order by o." + sort + " " + StringUtil.null2Str(dir));
		}else{
			hql.append(String.format(" order by o.%s desc ", pkName));
		}
		
		Query query = this.entityManager.createQuery(hql.toString());
		this.setHqlParamValue(query, paramMap);
		this.setHqlParamValue(query, paramOrMap);
		query.setFirstResult(start > 0 ? start : 0);
		query.setMaxResults(limit > 0 && limit < QUERY_PAGE_MAX_SIZE ? limit : QUERY_PAGE_MAX_SIZE);
		List<T> list = query.getResultList();
		entityManager.flush();
		return list;
    }
    
	@Override
	public List<T> getByIdList(List<Long> idList) {
		List<T> list = new ArrayList<T> ();
		if(idList == null || idList.size() == 0) return list;
		
		String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		String jpa = String.format("from %s where %s in (?1)", entityName, pkName);
		Query query = this.entityManager.createQuery(jpa);
		query.setParameter(1, idList);
		List<T> l = query.getResultList();
		entityManager.flush();
		return l;
	}
	

	@Override
	@Transactional
	public void deleteByIdList(List<Long> idList) {
		if(idList == null || idList.size() == 0) 
			return;
		
		String entityName = sqlBuilder.getEntityName(entityClass) ;
		String pkName = sqlBuilder.getPkField(entityClass);
		String jpa = String.format("delete from %s e where e.%s in (:idList)", entityName, pkName);
		Query query = this.entityManager.createQuery(jpa);
		query.setParameter("idList", idList);
		query.executeUpdate();
	}
	
	@Override
	@Transactional
	public List<T> batchInsert(List<T> modelList, int commitPerCount) {
		List<T> tModelLst = new ArrayList<T>();
		if (null == modelList || modelList.size() < 1)
			return null;
		int i = 0;
		int commitInCount = commitPerCount;
		if (commitInCount == 0 || commitInCount > 500)
			commitInCount = 100;// 默认100条提交一次
		
		try {
			for (T model : modelList) {
				model = this.save(model);
				tModelLst.add(model);
				i++;
				if (i % commitInCount == 0) {
					this.entityManager.flush();
					this.entityManager.clear();
				}
			}
			this.entityManager.flush();
			this.entityManager.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tModelLst;
	}
	
	public String getHqlParamKey(Map<String, Object> paramMap, Map<String, Object> paramOrMap){
		StringBuffer hqlParamKey = new StringBuffer();
		Set<String> setParamKey = new HashSet<String> ();
		if(paramMap != null && paramMap.size() > 0){
			boolean isFirst = true;
			for(Entry<String, Object> e : paramMap.entrySet()){
				String key = e.getKey();
				Object obj = e.getValue();
				if(key.indexOf(".") > 0){
					String objName = key.substring(0, key.indexOf("."));
					setParamKey.add(String.format("o.%s is not null ", objName));
				}
				
				if(!isFirst) hqlParamKey.append(" and ");
				hqlParamKey.append(setHqlParamKey(key, obj));
				isFirst = false;
				
			}
		}
		
		if(paramOrMap != null && paramOrMap.size() > 0){
			if(paramMap != null && paramMap.size() > 0) hqlParamKey.append(" and ");
			StringBuffer hqlOrParamKey = new StringBuffer();
			boolean isFirst = true;
			for(Entry<String, Object> e : paramOrMap.entrySet()){
				String key = e.getKey();
				Object obj = e.getValue();
				if(key.indexOf(".") > 0){
					String objName = key.substring(0, key.indexOf("."));
					setParamKey.add(String.format("o.%s is not null ", objName));
				}
				
				if(!isFirst) hqlOrParamKey.append(" or ");
				hqlOrParamKey.append(setHqlParamKey(key, obj));
				isFirst = false;
			}
			hqlParamKey.append(String.format(" (%s)", hqlOrParamKey.toString())); 
		}
		
		StringBuffer hqlParam = new StringBuffer ();
		if(hqlParamKey != null && !StringUtil.isNullStr(hqlParamKey.toString())){
			hqlParam.append(" where ");
			if(setParamKey != null && setParamKey.size() > 0){
				for(Iterator<String> it = setParamKey.iterator(); it.hasNext(); ){
					hqlParam.append(it.next() + " and ");
				}
			}
			hqlParam.append(hqlParamKey.toString());
		}
		return hqlParam.toString();
	}
	
	public String setHqlParamKey(String key, Object obj){
		String tmpKey = key.replace(".", "");
		if(obj instanceof List && tmpKey.contains("not in")){
			tmpKey = tmpKey.replaceAll("\\s+", "");
			return String.format("o.%s (:%s)", key, tmpKey);
		}else if(obj instanceof List){
			return String.format("o.%s in(:%s)", key, tmpKey);
		}else if(obj instanceof String && (StringUtil.null2Str(obj).startsWith("%") || StringUtil.null2Str(obj).endsWith("%"))){
			return String.format("o.%s like :%s", key, tmpKey);
		}else if(obj instanceof Date){
			String dateFormat = "%Y-%m-%d";
			String strkey = key.replace("<>", "").replace("<=", "").replace(">=", "").replace("<", "").replace(">", "").replace("=", ""); 
			String strTmpKey = tmpKey.replace("<>", "A").replace("<=", "B").replace(">=", "C").replace("<", "D").replace(">", "E").replace("=", "F"); 
			
			if(key.endsWith("<>")){
				return String.format("(o.%s is null or date_format(o.%s, '%s') <>:%s)", strkey, strkey, dateFormat, strTmpKey);
			}else if(key.endsWith(">=")){
				return String.format("date_format(o.%s, '%s') >=:%s", strkey, dateFormat, strTmpKey);
			}else if(key.endsWith("<=")){
				return String.format("date_format(o.%s, '%s') <=:%s", strkey, dateFormat, strTmpKey);
			}else if(key.endsWith(">")){
				return String.format("date_format(o.%s, '%s') >:%s", strkey, dateFormat, strTmpKey);
			}else if(key.endsWith("<")){
				return String.format("date_format(o.%s, '%s') <:%s", strkey, dateFormat, strTmpKey);
			}else{
				return String.format("date_format(o.%s, '%s') =:%s", strkey, dateFormat, strTmpKey);
			}
		}else{
			String strkey = key.replace("<>", "").replace("<=", "").replace(">=", "").replace("<", "").replace(">", "").replace("=", ""); 
			String strTmpKey = tmpKey.replace("<>", "SA").replace("<=", "SB").replace(">=", "SC").replace("<", "SD").replace(">", "SE").replace("=", "SF"); 
			if(key.endsWith("<>")){
				return String.format("(o.%s is null or o.%s <>:%s)", strkey, strkey, strTmpKey);
			}else if(key.endsWith(">=")){
				return String.format("o.%s >=:%s", strkey, strTmpKey);
			}else if(key.endsWith("<=")){
				return String.format("o.%s <=:%s", strkey, strTmpKey);
			}else if(key.endsWith(">")){
				return String.format("o.%s >:%s", strkey, strTmpKey);
			}else if(key.endsWith("<")){
				return String.format("o.%s <:%s", strkey, strTmpKey);
			}else{
				return String.format("o.%s =:%s", key, tmpKey);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void setHqlParamValue(Query query, Map<String, Object> paramMap){
		if(paramMap != null && paramMap.size() > 0){
			for(Entry<String, Object> e : paramMap.entrySet()){
				String key = e.getKey().replace(".", "");
				Object obj = e.getValue();
				if(obj instanceof List && key.contains("not in")){
					key = key.replaceAll("\\s+", "");
					query.setParameter(key, (List)obj);
				}else if(obj instanceof List){
					query.setParameter(key, (List)obj);
				}else if(obj instanceof Date){
					String strTmpKey = key.replace("<>", "A").replace("<=", "B").replace(">=", "C").replace("<", "D").replace(">", "E").replace("=", "F");  
					query.setParameter(strTmpKey,  DATE_PATTERN.format(obj));
				}else{
					String strTmpKey = key.replace("<>", "SA").replace("<=", "SB").replace(">=", "SC").replace("<", "SD").replace(">", "SE").replace("=", "SF"); 
					query.setParameter(strTmpKey, obj);
				}
			}
		}
	}

}