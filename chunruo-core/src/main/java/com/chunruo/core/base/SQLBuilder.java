package com.chunruo.core.base;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
 
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;

import com.chunruo.core.util.StringUtil;

@SuppressWarnings({"rawtypes","unchecked"})
public class SQLBuilder {
	 
    /**
     * 获取实体的名称
     * 
     * @param <T>
     * @param entityClass
     *            实体类
     * @return
     */
    public <T> String getEntityName(Class<T> entityClass) {
        String entityname = entityClass.getName();
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity.name() != null && !"".equals(entity.name())) {
            entityname = entity.name();
        }
        
        if(StringUtil.null2Str(entityname).contains(".")){
        	entityname = entityname.substring(entityname.lastIndexOf(".") + 1);
        }
        return entityname;
    }
     
    /**
     * 创建Select后所要查询的字段名称字符串
     * @author slx
     * @date 2009-7-8 上午10:01:02
     * @modifyNote
     * @param fields 
     *          需要查询的字段
     * @param alias  
     *          表的别名
     * @return
     *          拼接成的字段名字符串
     */
    public String buildSelect(String[] fields, String alias) {
        StringBuffer sf_select = new StringBuffer("SELECT");
        for (String field : fields) {
            sf_select.append(" ").append(alias).append(".").append(field)
                    .append(",");
        }
        return (sf_select.substring(0, sf_select.length() - 1)).toString();
    }
     
    /**
     * 创建Select后所要查询的字段名称字符串，并作为实体类的构造函数
     * @author yongtree
     * @date 2010-4-13 上午11:59:04
     * @modifyNote
     * @param fields
     * @param alias
     * @return
     */
    public String buildSelect(String className,String[] fields, String alias) {
        StringBuffer sf_select = new StringBuffer("SELECT new ").append(className).append("(");
        for (String field : fields) {
            sf_select.append(" ").append(alias).append(".").append(field)
                    .append(",");
        }
        return (sf_select.substring(0, sf_select.length() - 1))+")";
    }
     
     
    /**
     * 组装order by语句
     * 
     * @param orderby
     *      列名为key ,排序顺序为value的map
     * @return
     *      Order By 子句
     */
    public String buildOrderby(LinkedHashMap<String, String> orderby) {
        StringBuffer orderbyql = new StringBuffer("");
        if (orderby != null && orderby.size() > 0) {
            orderbyql.append(" order by ");
            for (String key : orderby.keySet()) {
                orderbyql.append("o.").append(key).append(" ").append(
                        orderby.get(key)).append(",");
            }
            orderbyql.deleteCharAt(orderbyql.length() - 1);
        }
        return orderbyql.toString();
    }
     
    /**
     * 得到Count聚合查询的聚合字段,既是主键列
     * @author slx
     * @date 2009-7-8 上午10:26:11
     * @modifyNote
     * @param <T>
     *              实体类型
     * @param clazz     
     *              实体类
     * @return
     *              聚合字段名(主键名)
     */
    public <T> String getPkField(Class<T> clazz) {
        String out = null;
        try {
            PropertyDescriptor[] propertyDescriptors = Introspector
                    .getBeanInfo(clazz).getPropertyDescriptors();
            for (PropertyDescriptor propertydesc : propertyDescriptors) {
                Method method = propertydesc.getReadMethod();
                if (method != null && method.isAnnotationPresent(Id.class)) {
                    out = propertydesc.getName();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
     
    /**
     * 设置查询参数
     * @author slx
     * @date 2009-7-8 上午10:02:55
     * @modifyNote
     * @param query 
     *          查询
     * @param queryParams
     *          查询参数
     */
    public Query setQueryParams(Query query, Object queryParams) {
        if (queryParams != null) {
            if (queryParams instanceof Object[]) {
                Object[] params = (Object[]) queryParams;
                if (params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        query.setParameter(i + 1, params[i]);
                    }
                }
            } else if (queryParams instanceof Map) {
				Map params = (Map) queryParams;
				Iterator<String> it = params.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();
                    query.setParameter(key, params.get(key));
                }
            }
        }
        return query;
    }
     
    /**
     * 将集合中的字符串拼接成为SQL语句中 in的形式 'aaa','bbb','ccc'
     * @author slx
     * @date 2009-5-26 上午10:30:17
     * @modifyNote
     * @param values
     * @return
     */
    public String toSQLIn(Collection<String> values){
        if(values == null || values.isEmpty())
            return null;
         
        String[] strvalues = new String[0];
        strvalues = (String[]) values.toArray(new String[values.size()]);
         
        return toSQLIn(strvalues);
    }
     
    /**
     * 将字符串数组中的字符串拼接成为SQL语句中 in的形式 'aaa','bbb','ccc'
     * @author slx
     * @date 2009-5-26 上午10:30:17
     * @modifyNote
     * @param values
     * @return
     */
    public String toSQLIn(String[] values){
        StringBuffer bf_sqlin = new StringBuffer();
        if(values == null || values.length == 0)
            return null;
         
        int len = values.length;
        for(int i = 0 ; i < len ; i++){
            bf_sqlin = bf_sqlin.append(", '").append(values[i]).append("' ");
        }
        String str_sqlin = bf_sqlin.substring(1).toString();
         
        return str_sqlin;
    }
}
